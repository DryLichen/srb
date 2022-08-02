<!-- 提供给路由的组件 -->
<template>
  <div class="app-container">
    <!-- 输入表单 -->
    <el-form label-width="120px">
      <el-form-item label="借款额度">
        <el-input-number v-model="integralGrade.borrowAmount" :min="0" />
      </el-form-item>
      <el-form-item label="积分区间开始">
        <el-input-number v-model="integralGrade.integralStart" :min="0" />
      </el-form-item>
      <el-form-item label="积分区间结束">
        <el-input-number v-model="integralGrade.integralEnd" :min="0" />
      </el-form-item>
      <el-form-item>
        <el-button :disabled="saveBtnDisabled" type="primary" @click="saveOrUpdate()">
          保存
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import integralGradeApi from "@/api/core/integral-grade";

//export default 向外暴露的成员，可以使用任意变量来接收
export default {
  data(){
    return{
      integralGrade: {},
      saveBtnDisabled: false
    }
  },

  //保证在渲染时已经查询到有效数据
  //获取路由中的id数据
  created() {
    if(this.$route.params.id){
      this.fetchDataById(this.$route.params.id)
    }
  },

  methods: {
    // 新增和更新的 保存按钮 对应方法
    saveOrUpdate(){
      //禁用保存按钮，防止重复提交
      this.saveBtnDisabled = true
      //如果有id值，就执行更新操作，否则执行新增操作
      if(this.integralGrade.id){
        this.updateData()
      } else {
        this.saveData()
      }
    },
    //新增数据
    saveData(){
      integralGradeApi.save(this.integralGrade).then(
        response => {
          this.$message({
            type: 'success',
            message: response.message
          })
        // 新增成功后跳转回列表页面
        this.$router.push('/core/integral-grade/list')
        }
      )
    },
    //回显数据，更新数据需要使用
    fetchDataById(id){
      integralGradeApi.getById(id).then(response => {
        this.integralGrade = response.data.record
      })
    },
    // 更新积分等级数据
    updateData(){
      integralGradeApi.update(this.integralGrade).then(response =>{
        this.$message({
          type: 'success',
          message: response.message
        })
        //  更新成功后跳转回列表页面
        this.$router.push('/core/integral-grade/list')
      })
    }
  }
}
</script>

<style scoped>

</style>
