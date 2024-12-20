<template>
  <div class="common-layout" id="main">
    <el-container class="parent-container">
      <el-header style="border-bottom: solid 1px;">
       <ShowHeader/>
      </el-header>
      <el-container class="child_main">
        <el-aside width="150px" class="aside">
          <NavigationAside @update-name-view="updateNameView" />
        </el-aside>
        <el-main>
          <router-view :name="name_view"/>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import NavigationAside from "@/components/NavigationAside";
import ShowHeader from "@/components/ShowHeader";
export default {
  name: "MainPage",
  components: {
    NavigationAside,
    ShowHeader
  },
  data() {
    return {
      name_view: "ServerManager"
    }
  },
  methods: {
    updateNameView(name) {
      this.name_view = name;
    }
  },
  mounted() {
  }
}
</script>

<style>
.parent-container {
  position: fixed;
  inset: 0;
}

.child_main {
  background-color: #fff;
}

</style>