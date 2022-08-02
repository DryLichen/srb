<!-- 提供给路由的组件 -->
<template>
  <div class="app-container">
    <!-- 将被数据渲染的表格，使用element-ui组件 -->
    <el-table v-bind:data="list" border stripe>
      <el-table-column type="index" width="50" label="序号"/>
      <el-table-column prop="borrowAmount" label="借款额度"/>
      <el-table-column prop="integralStart" label="积分区间开始"/>
      <el-table-column prop="integralEnd" label="积分区间结束"/>
      <el-table-column label="操作" width="200" align="center">
        <template v-slot="scope">
          <!-- 向vue方法传入id值 -->
          <el-button type="danger" icon="el-icon-delete" @click="removeById(scope.row.id)" circle/>
          <router-link :to="'/core/integral-grade/edit/'+scope.row.id" style="margin-right:5px;">
            <el-button type="primary" size="mini" icon="el-icon-edit">
              修改
            </el-button>
          </router-link>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
// 导入axios对象
import integralGradeApi from "@/api/core/integral-grade";

export default {
  //定义数据模型
  data(){
    return{
      list: []
    }
  },

  //页面渲染前获取数据
  created() {
    this.fetchData()
  },

  methods: {
    // 获取积分等级列表
    fetchData() {
      integralGradeApi.list().then(response => {
          this.list = response.data.list
      })
    },
    // 删除积分等级
    removeById(id) {
      // element-ui 的弹窗功能 messageBox
      this.$confirm('此操作将永久删除该记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then( () => {
        return integralGradeApi.removeById(id)
      }).then( response => {
        this.$message({
          type: 'success',
          message: response.message
        })
        this.fetchData()
      }).catch( error => {
        if(error === 'cancel'){
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        }
      })
    }
  }

}
</script>

<style scoped>

</style>
