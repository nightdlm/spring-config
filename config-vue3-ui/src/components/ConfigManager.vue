<template>
  <div class="config-manager">
    <!-- 服务选择卡片 -->
    <el-card class="select-card" shadow="hover">
      <div class="card-header">
        <div class="header-title">
          <el-icon><Document /></el-icon>
          <span>配置管理</span>
        </div>
      </div>
      <el-select 
        v-model="value" 
        filterable 
        placeholder="请选择服务" 
        style="width: 100%"
        @change="handleChange"
        clearable>
        <el-option
          v-for="item in options"
          :key="item.id"
          :label="item.serverName"
          :value="item.id">
        </el-option>
      </el-select>
    </el-card>

    <!-- 操作按钮 -->
    <el-card v-if="value" class="action-card" shadow="hover">
      <el-button type="primary" @click="openAddDialog">
        <el-icon style="margin-right: 5px"><Plus /></el-icon>
        新增配置
      </el-button>
    </el-card>

    <!-- 配置列表表格 -->
    <el-card v-if="value" class="table-card" shadow="hover">
      <el-table
        :data="value_list"
        style="width: 100%"
        v-loading="loading"
        stripe
        :header-cell-style="{background: '#f5f7fa', color: '#606266'}"
        empty-text="暂无配置数据">
        <el-table-column type="index" label="序号" width="80" align="center"/>
        <el-table-column prop="configKey" label="配置键" min-width="150">
          <template #default="scope">
            <div class="key-cell">
              <el-icon><Key /></el-icon>
              <span>{{ scope.row.configKey }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="value" label="配置值" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip/>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center"/>
        <el-table-column prop="updateTime" label="更新时间" width="170" align="center"/>
        <el-table-column fixed="right" label="操作" width="320" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="updateConfigInfo(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button link type="success" size="small" @click="publishUpdateData(scope.row.id)">
              <el-icon><Promotion /></el-icon>
              发布
            </el-button>
            <el-button link type="warning" size="small" @click="showHistory(scope.row)">
              <el-icon><Clock /></el-icon>
              历史
            </el-button>
            <el-popconfirm
              title="确定要删除此配置吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="delDynamicConfig(scope.row.id)">
              <template #reference>
                <el-button link type="danger" size="small">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end" />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑配置' : '添加配置'" 
      width="600px"
      :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="配置键" prop="key">
          <el-input v-model="form.key" placeholder="请输入配置键" :disabled="isEdit" />
        </el-form-item>
        
        <el-form-item label="数据类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择数据类型" style="width: 100%">
            <el-option label="布尔类型" :value="1" />
            <el-option label="数字类型" :value="2" />
            <el-option label="文本类型" :value="3" />
            <el-option label="JSON类型" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="配置值" prop="value">
          <el-input 
            v-model="form.value" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入配置值" />
        </el-form-item>

        <el-form-item label="描述" prop="desc">
          <el-input 
            v-model="form.desc" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入配置描述" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 历史记录对话框 -->
    <el-dialog 
      v-model="historyDialogVisible" 
      title="配置历史" 
      width="900px"
      :close-on-click-modal="false">
      <el-table :data="historyList" stripe>
        <el-table-column prop="id" label="版本ID" width="80" />
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="scope">
            <el-tag :type="getOperationTypeTag(scope.row.operationType)">
              {{ scope.row.operationType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="createTime" label="操作时间" width="170" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="showDiff(scope.row)">
              查看差异
            </el-button>
            <el-button link type="warning" size="small" @click="rollbackVersion(scope.row)" 
                       v-if="scope.row.operationType !== 'DELETE'">
              回滚
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- Diff对比对话框 -->
    <el-dialog 
      v-model="diffDialogVisible" 
      title="配置差异对比" 
      width="800px"
      :close-on-click-modal="false">
      <div v-if="diffData" class="diff-container">
        <div class="diff-info">
          <p><strong>操作类型:</strong> {{ diffData.history.operationType }}</p>
          <p><strong>操作人:</strong> {{ diffData.history.operatorName }}</p>
          <p><strong>操作时间:</strong> {{ diffData.history.createTime }}</p>
        </div>
        <div class="diff-content" v-html="diffData.htmlDiff"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getCurrentInstance } from 'vue';
import { Document, Plus, Edit, Delete, Promotion, Key, Clock } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const { proxy } = getCurrentInstance();

const value = ref('');
const options = ref([]);
const value_list = ref([]);
const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref(null);

// 历史记录
const historyDialogVisible = ref(false);
const historyList = ref([]);
const currentConfigId = ref(null);

// Diff对比
const diffDialogVisible = ref(false);
const diffData = ref(null);

// 分页
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 表单数据
const form = reactive({
  id: '',
  key: '',
  value: '',
  desc: '',
  type: 3
});

// 表单验证规则
const rules = {
  key: [
    { required: true, message: '请输入配置键', trigger: 'blur' }
  ],
  value: [
    { required: true, message: '请输入配置值', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择数据类型', trigger: 'change' }
  ]
};

const getServerList = async () => {
  try {
    const res = await proxy.$axios.post('/api/getServerList', {});
    options.value = res.data.data || [];
  } catch (error) {
    console.error('获取服务列表失败:', error);
  }
};

const handleChange = async (id) => {
  if (!id) {
    value_list.value = [];
    total.value = 0;
    return;
  }
  
  loading.value = true;
  try {
    const res = await proxy.$axios.post('/api/getList/' + id, {});
    const data = res.data.data || [];
    total.value = data.length;
    // 简单分页处理
    value_list.value = data.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
  } catch (error) {
    console.error('获取配置列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const openAddDialog = () => {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
};

const resetForm = () => {
  form.id = '';
  form.key = '';
  form.value = '';
  form.desc = '';
  form.type = 3;
  if (formRef.value) {
    formRef.value.clearValidate();
  }
};

const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    
    submitting.value = true;
    try {
      await proxy.$axios.post('/api/updateInfo', {
        id: form.id || null,
        serverId: value.value,
        key: form.key,
        value: form.value,
        desc: form.desc
      });
      
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功');
      dialogVisible.value = false;
      await handleChange(value.value);
    } catch (error) {
      console.error('提交失败:', error);
    } finally {
      submitting.value = false;
    }
  });
};

const delDynamicConfig = async (id) => {
  try {
    await proxy.$axios.delete('/api/delete/' + id);
    ElMessage.success('删除成功');
    await handleChange(value.value);
  } catch (error) {
    console.error('删除失败:', error);
  }
};

const updateConfigInfo = (row) => {
  isEdit.value = true;
  form.id = row.id;
  form.key = row.configKey;
  form.value = row.value;
  form.desc = row.description || '';
  form.type = 3; // 默认文本类型
  dialogVisible.value = true;
};

const publishUpdateData = async (id) => {
  try {
    await proxy.$axios.get('/api/publish/' + id);
    ElMessage.success('发布成功');
  } catch (error) {
    console.error('发布失败:', error);
  }
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  handleChange(value.value);
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  handleChange(value.value);
};

// 显示历史记录
const showHistory = async (row) => {
  currentConfigId.value = row.id;
  try {
    const res = await proxy.$axios.get(`/api/history/${row.id}`);
    historyList.value = res.data.data || [];
    historyDialogVisible.value = true;
  } catch (error) {
    console.error('获取历史记录失败:', error);
    ElMessage.error('获取历史记录失败');
  }
};

// 显示Diff对比
const showDiff = async (historyRow) => {
  try {
    const res = await proxy.$axios.get(`/api/diff/${historyRow.id}`);
    diffData.value = res.data.data;
    diffDialogVisible.value = true;
  } catch (error) {
    console.error('获取差异对比失败:', error);
    ElMessage.error('获取差异对比失败');
  }
};

// 回滚版本
const rollbackVersion = async (historyRow) => {
  try {
    await ElMessageBox.confirm(
      `确定要回滚到版本 ${historyRow.id} 吗？`,
      '回滚确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    await proxy.$axios.post(`/api/rollback/${historyRow.id}`);
    ElMessage.success('回滚成功');
    historyDialogVisible.value = false;
    await handleChange(value.value);
  } catch (error) {
    if (error !== 'cancel') {
      console.error('回滚失败:', error);
      ElMessage.error(error.response?.data?.message || '回滚失败');
    }
  }
};

// 获取操作类型标签颜色
const getOperationTypeTag = (type) => {
  const typeMap = {
    'CREATE': 'success',
    'UPDATE': 'primary',
    'DELETE': 'danger',
    'ROLLBACK': 'warning'
  };
  return typeMap[type] || 'info';
};

onMounted(() => {
  getServerList();
});
</script>

<style scoped>
.config-manager {
  max-width: 1400px;
}

.select-card,
.action-card,
.table-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.card-header {
  margin-bottom: 20px;
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

.key-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  color: #667eea;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-button--primary) {
  color: #667eea;
}

:deep(.el-button--success) {
  color: #67c23a;
}

:deep(.el-button--danger) {
  color: #f56c6c;
}

:deep(.el-pagination) {
  padding: 10px 0;
}

.diff-container {
  max-height: 600px;
  overflow-y: auto;
}

.diff-info {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 15px;
}

.diff-info p {
  margin: 5px 0;
  color: #606266;
}

.diff-content {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  background-color: #fafafa;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
}

.diff-line {
  padding: 2px 10px;
  white-space: pre-wrap;
  word-break: break-all;
}

.diff-line.added {
  background-color: #e1f3d8;
  color: #67c23a;
}

.diff-line.removed {
  background-color: #fde2e2;
  color: #f56c6c;
}

.diff-line.unchanged {
  color: #909399;
}
</style>