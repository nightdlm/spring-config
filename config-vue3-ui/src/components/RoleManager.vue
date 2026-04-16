<template>
  <div class="role-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon><UserFilled /></el-icon>
            角色管理
          </span>
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            新建角色
          </el-button>
        </div>
      </template>

      <el-table :data="roleList" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showPermissionDialog(row)">
              配置权限
            </el-button>
            <el-button link type="primary" size="small" @click="showEditDialog(row)">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="deleteRole(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑角色对话框 -->
    <el-dialog 
      v-model="roleDialogVisible" 
      :title="isEdit ? '编辑角色' : '创建角色'" 
      width="500px"
    >
      <el-form :model="roleForm" :rules="roleRules" ref="roleFormRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码（英文）" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="roleForm.description" type="textarea" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRole" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 配置权限对话框 -->
    <el-dialog 
      v-model="permissionDialogVisible" 
      title="配置权限" 
      width="600px"
    >
      <div v-if="currentRole">
        <p style="margin-bottom: 15px;">
          当前角色：<strong>{{ currentRole.roleName }}</strong> ({{ currentRole.roleCode }})
        </p>
        <el-tree
          ref="permissionTreeRef"
          :data="permissionTree"
          :props="{ label: 'permName', children: 'children' }"
          node-key="id"
          show-checkbox
          default-expand-all
        />
      </div>
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPermissions" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const roleList = ref([])
const permissionTree = ref([])
const roleDialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const isEdit = ref(false)
const currentRole = ref(null)
const roleFormRef = ref(null)
const permissionTreeRef = ref(null)

const roleForm = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const roleRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

// 加载角色列表
const loadRoles = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/role/list')
    roleList.value = res.data.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 加载权限树
const loadPermissions = async () => {
  try {
    const res = await axios.get('/api/permission/list')
    const allPermissions = res.data.data || []
    
    // 构建树形结构
    permissionTree.value = buildTree(allPermissions, 0)
  } catch (error) {
    ElMessage.error('获取权限列表失败')
  }
}

// 构建树形结构
const buildTree = (list, parentId) => {
  return list
    .filter(item => item.parentId === parentId)
    .map(item => ({
      ...item,
      children: buildTree(list, item.id)
    }))
}

// 显示创建对话框
const showCreateDialog = () => {
  isEdit.value = false
  resetForm()
  roleDialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (role) => {
  isEdit.value = true
  currentRole.value = role
  Object.assign(roleForm, {
    id: role.id,
    roleName: role.roleName,
    roleCode: role.roleCode,
    description: role.description,
    status: role.status
  })
  roleDialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  roleForm.id = null
  roleForm.roleName = ''
  roleForm.roleCode = ''
  roleForm.description = ''
  roleForm.status = 1
  if (roleFormRef.value) {
    roleFormRef.value.clearValidate()
  }
}

// 提交角色
const submitRole = async () => {
  if (!roleFormRef.value) return
  
  await roleFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      const url = isEdit.value ? '/role/update' : '/role/create'
      await axios.post(url, roleForm)
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      roleDialogVisible.value = false
      await loadRoles()
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

// 删除角色
const deleteRole = async (role) => {
  try {
    await ElMessageBox.confirm(`确定要删除角色 ${role.roleName} 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await axios.delete(`/role/${role.id}`)
    ElMessage.success('删除成功')
    await loadRoles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

// 显示权限配置对话框
const showPermissionDialog = async (role) => {
  currentRole.value = role
  await loadPermissions()
  
  // 获取该角色已有的权限
  try {
    const res = await axios.get(`/api/role/permissions/${role.id}`)
    const checkedKeys = res.data.data || []
    
    // 设置选中的节点
    setTimeout(() => {
      if (permissionTreeRef.value) {
        permissionTreeRef.value.setCheckedKeys(checkedKeys)
      }
    }, 100)
  } catch (error) {
    console.error('获取角色权限失败', error)
  }
  
  permissionDialogVisible.value = true
}

// 提交权限配置
const submitPermissions = async () => {
  if (!currentRole.value || !permissionTreeRef.value) return
  
  submitting.value = true
  try {
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
    
    // 合并全选和半选的节点ID
    const allKeys = [...checkedKeys, ...halfCheckedKeys]
    
    await axios.post('/role/assign-permissions', allKeys, {
      params: { roleId: currentRole.value.id }
    })
    
    ElMessage.success('权限配置成功')
    permissionDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '配置失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadRoles()
})
</script>

<style scoped>
.role-manager {
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
