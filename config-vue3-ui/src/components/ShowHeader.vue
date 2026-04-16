<template>
  <div class="header-container">
    <div class="header-left">
      <h2 class="header-title">动态配置管理系统</h2>
    </div>
    <div class="header-right">
      <el-dropdown trigger="click" v-if="userInfo">
        <div class="user-info">
          <el-avatar :size="36" src="@/assets/logo.png" />
          <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled>
              <el-icon><User /></el-icon>
              <span>{{ userInfo.nickname || userInfo.username }}</span>
            </el-dropdown-item>
            <el-dropdown-item @click="$emit('change-password')">
              <el-icon><Key /></el-icon>
              <span>修改密码</span>
            </el-dropdown-item>
            <el-dropdown-item divided @click="$emit('logout')">
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
import { User, SwitchButton, Key } from '@element-plus/icons-vue';

defineProps({
  userInfo: {
    type: Object,
    default: null
  }
})

defineEmits(['logout', 'change-password'])
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