<template>
  <div class="header-container">
    <div class="header-left">
      <h2 class="header-title">动态配置管理系统</h2>
    </div>
    <div class="header-right">
      <el-dropdown trigger="click">
        <div class="user-info">
          <el-avatar :size="36" src="@/assets/logo.png" />
          <span class="username">管理员</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled>
              <el-icon><User /></el-icon>
              <span>管理员</span>
            </el-dropdown-item>
            <el-dropdown-item divided disabled>
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { User, SwitchButton } from '@element-plus/icons-vue';
</script>

<style scoped>
.header-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  letter-spacing: 1px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.username {
  font-size: 14px;
  color: white;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>