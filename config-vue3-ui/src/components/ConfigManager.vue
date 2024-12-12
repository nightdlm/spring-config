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
    <el-col :span="24" :offset="23">
      <el-button type="primary">新增</el-button>
    </el-col>
  </el-row>

  <el-row>
    <!--      表格数据-->
    <el-col :span="24">
      <el-table
          border
          stripe="stripe"
          :data="value_list"
          style="width: 100%"
      >
        <el-table-column fixed prop="config_key" label="配置名"/>
        <el-table-column prop="config_value" label="配置值"/>
        <el-table-column prop="upd_time" label="更新时间"/>
        <el-table-column fixed="right" label="操作" min-width="120" width="200px">
          <template #default>
            <el-button link type="primary" size="small">删除</el-button>
            <el-button link type="primary" size="small">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-col>
  </el-row>
</template>

<script>
// import {ElMessage} from "element-plus";

export default {
  name: "ConfigManager",
  data() {
    return {
      value: '',
      options: [],
      value_list: []
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
    handleChange(val) {
      console.log(val);
    }
  },
  mounted() {
    this.getServerList()
  }
}
</script>

<style scoped>

</style>