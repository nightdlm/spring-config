<template>
  <el-row class="tac">
    <el-col>
      <el-menu style="height: 100%;" default-active="1" mode="vertical">
        <el-menu-item index="1" @click="goto('ServerManager')">服务管理</el-menu-item>
        <el-menu-item index="2" @click="goto('ConfigManager')">配置管理</el-menu-item>
<!--        <el-menu-item index="3" @click="goto('AboutInfo')">关于</el-menu-item>-->
      </el-menu>
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: "NavigationAside",
  props: {
    name: String
  },
  data() {
    return {
      name_view: this.name
    }
  },
  methods: {
    goto(viewName) {
      this.$emit('update-name-view', viewName);
    }
  },
  mounted() {
  }
}
</script>

<style scoped>
.tac {
  background-color: #9fdbf8;
  height: 100%;
}



</style>