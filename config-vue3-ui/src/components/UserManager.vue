<template>
  <div class="user-manager">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </div>
          <el-button type="primary" @click="showCreateDialog" v-if="isAdmin">
            <el-icon><Plus /></el-icon>
            创建用户
          </el-button>
        </div>
      </template>

      <el-table :data="userList" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'primary'">
              {{ scope.row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" align="center" v-if="isAdmin">
          <template #default="scope">
            <el-button link type="warning" size="small" @click="showResetPasswordDialog(scope.row)">
              重置密码
            </el-button>
            <el-button link type="danger" size="small" @click="deleteUser(scope.row)" 
                       v-if="scope.row.username !== 'admin'">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建用户对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建用户" width="500px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="createForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="createForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="createForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="createForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="createForm.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option 
              v-for="role in roles" 
              :key="role.id" 
              :label="role.roleName" 
              :value="role.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="createForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreateUser" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPasswordDialogVisible" title="重置密码" width="400px">
      <el-form :model="resetForm" :rules="resetRules" ref="resetFormRef" label-width="100px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitResetPassword" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="changePasswordDialogVisible" title="修改密码" width="400px">
      <el-form :model="changePasswordForm" :rules="changePasswordRules" ref="changePasswordFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="changePasswordForm.oldPassword" type="password" placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="changePasswordForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitChangePassword" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const userList = ref([])
const createDialogVisible = ref(false)
const resetPasswordDialogVisible = ref(false)
const changePasswordDialogVisible = ref(false)
const createFormRef = ref(null)
const resetFormRef = ref(null)
const changePasswordFormRef = ref(null)
const currentUser = ref(null)
const selectedUserId = ref(null)

const isAdmin = computed(() => {
  return currentUser.value?.role === 'ADMIN'
})

const createForm = reactive({
  username: '',
  password: '',
  nickname: '',
  email: '',
  roleId: null,
  status: 1
})


const changePasswordForm = reactive({
  oldPassword: '',
  newPassword: ''
})

const roles = ref([])

// const loadRoles = async () => {
//   try {
//     const res = await axios.get('/api/role/list')
//     roles.value = res.data.data || []
//   } catch (error) {
//     console.error('获取角色列表失败', error)
//   }
// }

const createRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const resetRules = {
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

const changePasswordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
}

const loadUserList = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/user/list')
    userList.value = res.data.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  createDialogVisible.value = true
  resetForm()
}

const resetForm = () => {
  createForm.username = ''
  createForm.password = ''
  createForm.nickname = ''
  createForm.email = ''
  createForm.roleId = null
  createForm.status = 1
  if (createFormRef.value) {
    createFormRef.value.clearValidate()
  }
}

const submitCreateUser = async () => {
  if (!createFormRef.value) return
  
  await createFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await axios.post('/api/user/create', createForm)
      ElMessage.success('创建成功')
      createDialogVisible.value = false
      await loadUserList()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '创建失败')
    } finally {
      submitting.value = false
    }
  })
}

const showResetPasswordDialog = (user) => {
  selectedUserId.value = user.id
  resetPasswordDialogVisible.value = true
  resetForm.newPassword = ''
  if (resetFormRef.value) {
    resetFormRef.value.clearValidate()
  }
}

const submitResetPassword = async () => {
  if (!resetFormRef.value) return
  
  await resetFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await axios.post(`/api/user/reset-password/${selectedUserId.value}`, null, {
        params: { newPassword: resetForm.newPassword }
      })
      ElMessage.success('重置密码成功')
      resetPasswordDialogVisible.value = false
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '重置密码失败')
    } finally {
      submitting.value = false
    }
  })
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 ${user.username} 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await axios.delete(`/api/user/${user.id}`)
    ElMessage.success('删除成功')
    await loadUserList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

const showChangePasswordDialog = () => {
  changePasswordDialogVisible.value = true
  changePasswordForm.oldPassword = ''
  changePasswordForm.newPassword = ''
  if (changePasswordFormRef.value) {
    changePasswordFormRef.value.clearValidate()
  }
}

const submitChangePassword = async () => {
  if (!changePasswordFormRef.value) return
  
  await changePasswordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await axios.post('/api/user/password', changePasswordForm)
      ElMessage.success('修改密码成功')
      changePasswordDialogVisible.value = false
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '修改密码失败')
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  const user = localStorage.getItem('user')
  if (user) {
    currentUser.value = JSON.parse(user)
    loadUserList()
  }
})

// 暴露方法给父组件
defineExpose({
  showChangePasswordDialog
})
</script>

<style scoped>
.user-manager {
  max-width: 1400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-title .el-icon {
  font-size: 20px;
  color: #667eea;
}
</style>
