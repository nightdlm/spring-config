<template>
  <div class="common-layout" id="main">
    <el-container class="parent-container">
      <el-header class="header">
       <ShowHeader/>
      </el-header>
      <el-container class="child_main">
        <el-aside width="200px" class="aside">
          <NavigationAside @update-name-view="updateNameView" />
        </el-aside>
        <el-main class="main-content">
          <router-view :name="name_view"/>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import NavigationAside from "@/components/NavigationAside";
import ShowHeader from "@/components/ShowHeader";

const name_view = ref("ServerManager");

const updateNameView = (name) => {
  name_view.value = name;
};
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