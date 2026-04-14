<template>
  <div class="server-manager">
    <!-- 顶部操作栏 -->
    <el-card class="operation-card" shadow="hover">
      <div class="card-header">
        <div class="header-title">
          <el-icon><Monitor /></el-icon>
          <span>服务管理</span>
        </div>
      </div>
      <el-row :gutter="20">
        <el-col :span="16">
          <el-input 
            v-model="server_name" 
            placeholder="请输入服务名称" 
            clearable
            @keyup.enter="addServerName">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="addServerName" :loading="adding">
            <el-icon style="margin-right: 5px"><Plus /></el-icon>
            添加服务
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 服务列表 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        :data="server_list"
        style="width: 100%"
        v-loading="loading"
        stripe
        :header-cell-style="{background: '#f5f7fa', color: '#606266'}"
        empty-text="暂无服务数据">
        <el-table-column type="index" label="序号" width="80" align="center"/>
        <el-table-column prop="serverName" label="服务名称" min-width="200">
          <template #default="scope">
            <div class="server-name-cell">
              <el-icon><Connection /></el-icon>
              <span>{{ scope.row.serverName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center"/>
        <el-table-column fixed="right" label="操作" width="150" align="center">
          <template #default="scope">
            <el-popconfirm
              title="确定要删除此服务吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="delServer(scope.row.id)">
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getCurrentInstance } from 'vue';
import { Monitor, Search, Plus, Delete, Connection } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const { proxy } = getCurrentInstance();

const server_name = ref('');
const server_list = ref([]);
const loading = ref(false);
const adding = ref(false);

const addServerName = async () => {
  if (!server_name.value.trim()) {
    ElMessage.warning('请输入服务名称');
    return;
  }
  
  adding.value = true;
  try {
    await proxy.$axios.post('/api/createServer?serverName=' + server_name.value, {});
    ElMessage.success('添加成功');
    server_name.value = '';
    await getServerList();
  } catch (error) {
    console.error('添加失败:', error);
  } finally {
    adding.value = false;
  }
};

const getServerList = async () => {
  loading.value = true;
  try {
    const res = await proxy.$axios.post('/api/getServerList', {});
    server_list.value = res.data.data || [];
  } catch (error) {
    console.error('获取服务列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const delServer = async (id) => {
  try {
    await proxy.$axios.post('/api/deleteServer?id=' + id, {});
    ElMessage.success('删除成功');
    await getServerList();
  } catch (error) {
    console.error('删除失败:', error);
  }
};

onMounted(() => {
  getServerList();
});
</script>

<style scoped>
.server-manager {
  max-width: 1200px;
}

.operation-card {
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

.table-card {
  border-radius: 8px;
}

.server-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.server-name-cell .el-icon {
  color: #667eea;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-button--danger) {
  color: #f56c6c;
}

:deep(.el-button--danger:hover) {
  color: #ff0000;
}
</style>