<template>
  <div class="app-container">
    <!-- 导入数据按钮 嵌入表单的对话框dialog在点击后为true显示-->
    <div style="margin-bottom: 10px;">
      <el-button @click="dialogVisible = true" type='primary' icon='el-icon-upload'>
        导入excel
      </el-button>
      <!-- 导出excel按钮 -->
      <el-button @click="exportData" type='primary' icon='el-icon-download'>
        导出Excel
      </el-button>
    </div>

    <!-- 对话框内嵌表单 -->
    <el-dialog title="数据字典导入" :visible.sync="dialogVisible" width="30%">
      <el-form>
        <el-form-item label="请选择Excel文件">
          <!-- 一般有on 或者是@ 都会有钩子函数 -->
          <!-- 这里并没有使用自定义的axios，而是el自带的axios 所以不受拦截器控制 -->
          <el-upload
            :auto-upload="true"
            :multiple="false"
            :limit="1"
            :on-exceed="fileUploadExceed"
            :on-success="fileUploadSuccess"
            :on-error="fileUploadError"
            :action="BASE_API + '/admin/core/dict/import'"
            name="file"
            accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          >
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <!-- dialog footer -->
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
      </div>
    </el-dialog>

    <!-- 树形表格数据模板 -->
    <!-- 延迟加载，加载函数为getChildren -->
    <el-table :data="list" border row-key="id" :load="getChildren" lazy>
      <el-table-column label="名称" align="left" prop="name" />
      <el-table-column label="编码" prop="dictCode" />
      <el-table-column label="值" align="left" prop="value" />
    </el-table>

  </div>
</template>

<script>
// 引入数据字典 api
import dictApi from '@/api/core/dict.js'

export default {
  data(){
    return {
      // 设置默认状态下对话框是否可见
      dialogVisible: false,
      BASE_API: process.env.VUE_APP_BASE_API,  //获取后端接口地址
      list: []  //树形表格数据
    }
  },

  created(){
    this.fetchData()
  },

  methods: {
    fileUploadExceed(){
      this.$message({
        type: 'warning',
        message: "一次只能导入一个文件！"
      })
    },
    // 前端上传成功
    fileUploadSuccess(response){
      // 后端处理成功
      if(response.code === 0){
        this.$message.success("数据导入成功！")
        this.dialogVisible = false
        // 优化：在上传excel文件后刷新数据字典表格
        this.fetchData()
      } else {
        this.$message.error(response.message)
      }
    },
    // 前端上传失败
    fileUploadError(error){
      this.$message.error("数据导入失败！")
    },

    // 2.导出数据到excel
    exportData(){
      window.location.href = this.BASE_API + "/admin/core/dict/export"
    },

    // 3.调用api获取数据，从表结构可知根id为1
    fetchData(){
      dictApi.listByParentId(1).then(response => {
        this.list = response.data.dictList
      })
    },
    // 延迟加载子节点
    getChildren(row, treeNode, resolve){
      // 加载节点对应的子节点数据
      dictApi.listByParentId(row.id).then(response => {
        resolve(response.data.dictList)
      })
    }
  }

}
</script>

<style scoped>

</style>
