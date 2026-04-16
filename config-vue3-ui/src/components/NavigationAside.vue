<template>
  <div class="nav-container">
    <div class="nav-logo">
      <span class="logo-text">配置中心</span>
    </div>
    <el-menu 
      :default-active="activeIndex" 
      class="nav-menu"
      @select="handleSelect">
      <el-menu-item index="ServerManager">
        <el-icon><Setting /></el-icon>
        <span>服务管理</span>
      </el-menu-item>
      <el-menu-item index="ConfigManager">
        <el-icon><Document /></el-icon>
        <span>配置管理</span>
      </el-menu-item>
      <el-menu-item index="UserManager" v-if="isAdmin">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item index="RoleManager" v-if="isAdmin">
        <el-icon><UserFilled /></el-icon>
        <span>角色管理</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Setting, Document, User, UserFilled } from '@element-plus/icons-vue';

const emit = defineEmits(['update-name-view']);
const activeIndex = ref('ServerManager');

const isAdmin = computed(() => {
  const user = localStorage.getItem('user')
  if (user) {
    const userInfo = JSON.parse(user)
    return userInfo.role === 'ADMIN'
  }
  return false
})

const handleSelect = (index) => {
  activeIndex.value = index;
  emit('update-name-view', index);
};
</script>

<style scoped>
.nav-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.nav-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 18px;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo-text {
  letter-spacing: 2px;
}

.nav-menu {
  flex: 1;
  border-right: none;
  padding: 10px 0;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 5px 10px;
  border-radius: 8px;
  transition: all 0.3s;
}

:deep(.el-menu-item:hover) {
  background-color: #f0f2f5 !important;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
}

:deep(.el-menu-item .el-icon) {
  margin-right: 10px;
  font-size: 18px;
}
</style>