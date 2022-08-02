<template>
  <div class="app-container">

    <!-- 查询条件表单 -->
    <el-form :inline='true'>
      <el-form-item label='关键词'>
        <el-input v-model='keyword' aria-placeholder='用户id/借款申请id'/>
      </el-form-item>
      <el-button type='primary' icon='el-icon-search' @click='fetchData()'>
        查询
      </el-button>
      <el-button type="default" @click='resetData()'>
        清空
      </el-button>
    </el-form>

    <!-- 标的列表 -->
    <el-table :data="list" stripe>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="lendNo" label="标的编号" width="160" />
      <el-table-column prop="amount" label="标的金额" />
      <el-table-column prop="period" label="投资期数" />
      <el-table-column label="年化利率">
        <template v-slot="scope">
          {{ scope.row.lendYearRate * 100 }}%
        </template>
      </el-table-column>
      <el-table-column prop="investAmount" label="已投金额" />
      <el-table-column prop="investNum" label="投资人数" />
      <el-table-column prop="publishDate" label="发布时间" width="150" />
      <el-table-column prop="lendStartDate" label="开始日期" />
      <el-table-column prop="lendEndDate" label="结束日期" />
      <el-table-column prop="param.returnMethod" label="还款方式" />
      <el-table-column prop="param.status" label="状态" />

      <el-table-column label="操作" width="150" align="center">
        <template v-slot="scope">
          <el-button type="primary" size="mini">
            <router-link :to="'/core/lend/detail/' + scope.row.id">
              查看
            </router-link>
          </el-button>
          <el-button
            v-if="scope.row.status == 1"
            type="warning"
            size="mini"
            @click="makeLoan(scope.row.id)"
          >
            放款
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <!-- el-ui其实不是物理分页，只是将后端传来的数据进行模拟分页，所以后端没有分页功能也可以用 -->
    <el-pagination
      :current-page="page"
      :total="total"
      :page-size="size"
      :page-sizes="[2, 10, 20]"
      style="padding: 30px 0"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="changeCurrentPage"
      @size-change="changePageSize"
    />

  </div>
</template>

<script>
import lendApi from '@/api/core/lend'

export default {
  data() {
    return {
      keyword: null,
      list: [],

      //分页数据
      page: 0,
      size: 10,
      total: null
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    fetchData(){
      lendApi.getList(this.keyword)
        .then( response => {
          this.list = response.data.list
          this.total = response.data.total
        })
    },
    resetData(){
      this.keyword = null
      this.fetchData()
    },

    //放款
    makeLoan(id){
      this.$confirm('确定放款吗?', '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then( () => {
          return lendApi.makeLoan(id)
      }).then( response => {
        this.fetchData()
        this.$message.success(response.message)
      }).catch( error => {
        console.log('取消', error)
        if(error == 'cancel'){
          this.$message.info('已取消放款')
        }
      })
    },

    //分页回调函数
    changeCurrentPage(page){
      this.page = page
      this.fetchData()
    },
    changePageSize(size){
      this.size = size
      this.fetchData()
    }
  }
}
</script>

<style scoped>

</style>
