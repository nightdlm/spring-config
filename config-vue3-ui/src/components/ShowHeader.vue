<template>
  <el-row style="width: 100vw;border-radius: 5px;border-bottom-color: black">

    <el-col style="text-align: right;line-height: 3.5;" :span="2" :offset="21">
        <a style="margin-right: 15px" class="position_ele" href="javaScript:void(0)" @click="click_test">登录</a>
    </el-col>

    <el-col :span="1">
      <img @click="click_test" class="user_header" draggable="false" style="" src="@/assets/logo.png" alt="头像">
    </el-col>
  </el-row>

</template>

<script>
export default {
  name: "ShowHeader",
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    click_test(){
      alert('点击')
    }
  }
}
</script>

<style scoped>

.user_header {
  border: solid 1px #ccc;
  margin-top: 10px;
  border-radius: 50%;
  background-color: white;
  width: 30px;
  height: 30px;
  cursor: pointer;
}
</style>