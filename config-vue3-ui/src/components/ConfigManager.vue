<template>
  <el-row>
    <!--      选择框-->
    <el-col :span="8">
      <el-select @change="handleChange" v-model="value" filterable placeholder="请选择" no-match-text="无匹配数据"
                 no-data-text="无数据">
        <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.serverName"
            :value="item.id">
        </el-option>
      </el-select>
    </el-col>
  </el-row>

  <el-divider/>

  <el-row>
    <el-col :span="24" :offset="22">
      <el-button type="primary" @click="dialogVisible = true" :disabled="value===''">新增</el-button>
    </el-col>
  </el-row>

  <el-row style="margin-top: 20px">
    <!--      表格数据-->
    <el-col :span="24">
      <el-table
          border
          stripe="stripe"
          :data="value_list"
          style="width: 100%"
      >
        <el-table-column type="index" min-width="80" width="80" label="序号"/>
        <el-table-column prop="configKey" label="配置名"/>
        <el-table-column prop="value" label="配置值"/>
        <el-table-column prop="createTime" label="创建时间"/>
        <el-table-column prop="updateTime" label="更新时间"/>
        <el-table-column prop="description" label="描述"/>
        <el-table-column fixed="right" label="操作" min-width="120" width="200px">
          <template #default>
            <el-button link type="primary" size="small">删除</el-button>
            <el-button link type="primary" size="small">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-col>
  </el-row>

<!--  添加弹出框-->
  <el-dialog title="添加配置" v-model="dialogVisible" width="30%">
    <el-input resize="none" size="large" type="textarea" v-model="desc" placeholder="placeholder"></el-input>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogVisible = false">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
export default {
  name: "ConfigManager",
  data() {
    return {
      value: '',
      options: [],
      value_list: [],
      desc: '',
      dialogVisible: false,
      input: ''
    }
  },
  methods: {
    getServerList(){
      const _this = this
      this.$axios.post('/api/getServerList', {})
          .then((res)=>{
            _this.options = res.data.data
          })
    },
    handleChange(id) {
      const _this = this
      this.$axios.post('/api/getList/'+id, {})
          .then((res)=>{
            _this.value_list = res.data.data
          })
    },
    addDynamicConfig(){
      const _this = this
      this.$axios.post('/api/create', {
        id:_this.value,
        key:"recall.switch",
        value:true,
        desc:""
      })
      _this.getServerList()
    }
  },
  mounted() {
    this.getServerList()
  }
}
</script>

<style scoped>

</style>