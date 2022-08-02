<template>
  <div class='app-container'>

    <!--查询条件表单-->
    <el-form :inline='false'>
      <el-form-item label='关键词'>
        <el-input v-model='keyword' placeholder='姓名/手机号/身份证号'/>
      </el-form-item>
      <el-button type='primary' icon='el-icon-search' @click='fetchData()'>
        查询
      </el-button>
      <el-button type="default" @click="resetData()">
        清空
      </el-button>
    </el-form>

    <!--借款用户列表-->
    <el-table :data='list' stripe>
      <el-table-column label="序号" width="70" align="center">
        <template v-slot='scope'>
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>

      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="mobile" label="手机" />
      <el-table-column prop="idCard" label="身份证号" width="200" />
      <el-table-column label="性别" width="80">
        <template v-slot="scope">
          {{ scope.row.sex === 1 ? '男' : '女' }}
        </template>
      </el-table-column>

      <el-table-column prop="age" label="年龄" width="80" />

      <el-table-column label="是否结婚" width="120">
        <template v-slot="scope">
          {{ scope.row.marry ? '是' : '否' }}
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === 0" type="info" size="mini">
            未认证
          </el-tag>
          <el-tag v-if="scope.row.status === 1" type="warning" size="mini">
            认证中
          </el-tag>
          <el-tag v-if="scope.row.status === 2" type="success" size="mini">
            认证通过
          </el-tag>
          <el-tag v-if="scope.row.status === -1" type="danger" size="mini">
            认证失败
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="createTime" label="申请时间" width="160" />

      <!-- route到借款人详情页面 -->
      <el-table-column label="操作" width="200" align="center">
        <template v-slot="scope">
          <router-link :to="'/core/borrower/detail/' + scope.row.id">
            <el-button v-if="scope.row.status === 1" type="warning" size="mini">
              审批
            </el-button>
            <el-button v-else type="primary" size="mini">
              查看
            </el-button>
          </router-link>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      :current-page="page"
      :total="total"
      :page-size="limit"
      :page-sizes="[2, 10, 20]"
      style="padding: 30px 0"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="changePageSize"
      @current-change="changeCurrentPage"
    />

  </div>
</template>

<script>
import borrowerApi from '@/api/core/borrower'

export default {
  data(){
    return {
      page: 1,
      total: 1, //查询到的总数据量
      limit: 10,
      keyword: null,
      list: [],
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    // 获取展示借款人列表需要的数据
    fetchData(){
      borrowerApi.getListPage(this.page, this.limit, this.keyword).then( response => {
        this.list = response.data.pageModel.records
        this.total = response.data.pageNodel.total
      })
    },
    resetData(){
      this.keyword = null
      this.fetchData()
    },

    // 回调函数。size表示当前选中的每页数据条数
    changePageSize(size){
      this.limit = size
      this.fetchData()
    },
    changeCurrentPage(page){
      this.page = page
      this.fetchData()
    }
  }
}
</script>

<style scoped>

</style>
