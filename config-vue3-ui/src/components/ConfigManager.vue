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
          <template #default="scope">
            <el-button link type="primary" size="small" @click="delDynamicConfig(scope.row.id)">删除</el-button>
            <el-button link type="primary" size="small" @click="updateConfigInfo(scope.row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="publishUpdateData(scope.row.id)">发布</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-col>
  </el-row>

<!--  添加弹出框-->
  <el-dialog title="添加配置" v-model="dialogVisible" width="40%">

    <table>
      <tbody>
      <tr>
        <td>key:</td>
        <td>
          <el-input resize="none" size="large" type="text" v-model="input_key" placeholder="输入配置key"></el-input>
        </td>
      </tr>

      <tr>
        <td>数据类型:</td>
        <td>
          <el-select
              style="width: 150px"
              v-model="model"
              placeholder="数据类型">
            <el-option
                v-for="item in typeOption"
                :key=item.value
                :label=item.label
                :value=item.value>
            </el-option>
          </el-select>
        </td>
      </tr>

      <tr>
        <td style="vertical-align: top; text-align: left; min-width:100px;">配置值:</td>
        <td style="width: 100%;"><el-input resize="none" size="large" type="textarea" v-model="input_value" placeholder="输入配置值" rows=4></el-input></td>
      </tr>


      <tr>
        <td style="vertical-align: top; text-align: left; min-width:100px;">值描述:</td>
        <td style="width: 100%;"><el-input resize="none" size="large" type="textarea" v-model="desc" placeholder="说明信息" rows=4></el-input></td>
      </tr>
      </tbody>
    </table>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addDynamicConfig">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
export default {
  name: "ConfigManager",
  data() {
    return {
      model:3,
      typeOption:[
          {label:'布尔类型',value:1},
          {label:'数字类型',value:2},
          {label:'文本类型',value:3},
          {label:'JSON类型',value:4}
      ],
      value: '',
      options: [],
      value_list: [],
      desc: '',
      dialogVisible: false,
      input_key: '',
      input_value: '',
      key_id:''
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
      this.$axios.post('/api/updateInfo', {
        id:_this.key_id,
        serverId:_this.value,
        key:_this.input_key,
        value:_this.input_value,
        desc:_this.desc
      }).then(()=>{
        _this.dialogVisible = false
        _this.handleChange(_this.value)
        _this.key_id=''
      })
    },
    delDynamicConfig(id){

      const _this = this
      this.$confirm('此操作将永久删除该数据，请谨慎操作, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.delete('/api/delete/'+id).then(()=>{
          _this.handleChange(_this.value)
        })
        this.$message({
          type: 'success',
          message: '删除成功!'
        });
      }).catch(() => {

      });

    },
    //打开编辑弹框
    updateConfigInfo(row){
      const _this = this
      _this.key_id=''
      _this.dialogVisible = true
      _this.key_id=row.id
      _this.serverId=_this.value
      _this.input_key=row.configKey
      _this.input_value=row.value
      _this.desc=row.description
    },
    publishUpdateData(id){
      this.$axios.get('/api/publish/'+id).then(()=>{
        this.$message({
          message: '发布成功',
          type: 'success'
        });
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