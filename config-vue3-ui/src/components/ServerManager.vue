<template>

  <el-row>
    <el-col :span="24">
      <el-input v-model="server_name" placeholder="输入服务名" style="width: 400px; margin-right: 10px;"></el-input>
      <el-button type="primary" @click="addServerName">添加</el-button>
    </el-col>
  </el-row>

  <el-divider/>

  <el-row>
    <!--      表格数据-->
    <el-col :span="24">
      <el-table
          border
          stripe="stripe"
          :data="server_list"
          style="width: 100%"
      >
        <el-table-column type="index" label="序号" min-width="50" width="80px"/>
        <el-table-column prop="serverName" label="服务名"/>
        <el-table-column fixed="right" label="操作" min-width="120" width="200px">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="delServer(scope.row.id)">删除</el-button>
            <el-button link type="primary" size="small">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-col>
  </el-row>

</template>

<script>
export default {
  name: "ServerManager",
  data() {
    return {
      server_name: '',
      server_list: []
    }
  },
  methods: {
    addServerName(){
      const _this = this
      this.$axios.post('/api/createServer?serverName='+this.server_name, {})
          .then(()=>{
            _this.getServerList()
      })
    },
    getServerList(){
      const _this = this
      this.$axios.post('/api/getServerList', {})
          .then((res)=>{
            _this.server_list = res.data.data
      })
    },
    delServer(id){
      const _this = this
      this.$axios.post('/api/deleteServer?id='+id, {})
          .then(()=>{
              _this.getServerList()
      })
    }
  },
  mounted() {
    this.getServerList()
  }
}
</script>

<style scoped>

</style>