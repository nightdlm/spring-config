<template>
  <div class="common-layout" id="main">
    <el-container class="parent-container">
      <el-header class="header">
       <ShowHeader :user-info="userInfo" @logout="handleLogout" @change-password="handleChangePassword"/>
      </el-header>
      <el-container class="child_main">
        <el-aside width="200px" class="aside">
          <NavigationAside @update-name-view="updateNameView" />
        </el-aside>
        <el-main class="main-content">
          <router-view :name="name_view" />
          <UserManager v-show="false" ref="userManagerRef" />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import NavigationAside from "@/components/NavigationAside";
import ShowHeader from "@/components/ShowHeader";
import UserManager from "@/components/UserManager";
import axios from 'axios';
import { ElMessage } from 'element-plus';

const router = useRouter()
const name_view = ref("ServerManager");
const userInfo = ref(null);
const userManagerRef = ref(null);

const updateNameView = (name) => {
  name_view.value = name;
};

onMounted(() => {
  const user = localStorage.getItem('user')
  if (user) {
    userInfo.value = JSON.parse(user)
  }
})

const handleLogout = async () => {
  try {
    await axios.post('/api/auth/logout')
    localStorage.removeItem('user')
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error('退出失败')
  }
}

const handleChangePassword = () => {
  if (userManagerRef.value) {
    userManagerRef.value.showChangePasswordDialog()
  }
}
</script>

<style scoped>
.parent-container {
  position: fixed;
  inset: 0;
  background: #f5f7fa;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 60px;
  line-height: 60px;
}

.child_main {
  background-color: #f5f7fa;
}

.aside {
  background: #ffffff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  overflow-y: auto;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>