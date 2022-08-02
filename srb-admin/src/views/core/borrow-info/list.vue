<template>
  <div class='app-container'>

    <!-- 查询条件表单 -->
    <el-form :inline='true'>
      <el-form-item label='关键词'>
        <el-input v-model='keyword' aria-placeholder='姓名/手机号'/>
      </el-form-item>
      <el-button type='primary' icon='el-icon-search' @click='fetchData()'>
        查询
      </el-button>
      <el-button type="default" @click='resetData()'>
        清空
      </el-button>
    </el-form>

    <!-- 借款申请表格 -->
    <el-table :data='borrowInfoList' stripe>
      <el-table-column label="序号" width="60" align="center">
        <template v-slot='scope'>
          {{ (page - 1) * size + scope.$index + 1 }}
        </template>
      </el-table-column>

      <el-table-column prop="name" label="借款人姓名" width="90" />
      <el-table-column prop="mobile" label="手机" />
      <el-table-column prop="amount" label="借款金额" />
      <!-- 如果有特殊样式需求，使用template -->
      <el-table-column label="借款期限" width="90">
        <template v-slot="scope">
          {{ scope.row.period }}个月
        </template>
      </el-table-column>
      <el-table-column prop="param.returnMethod" label="还款方式" width="150" />
      <el-table-column prop="param.moneyUse" label="资金用途" width="100" />
      <el-table-column label="年化利率" width="90">
        <template v-slot="scope">
          {{ scope.row.borrowYearRate * 100 }}%
        </template>
      </el-table-column>
      <el-table-column prop="param.status" label="状态" width="100" />
      <el-table-column prop="createTime" label="申请时间" width="150" />

      <el-table-column label="操作" width="150" align="center">
        <template v-slot="scope">
          <el-button type="primary" size="mini">
            <router-link :to="'/core/borrower/info/detail/' + scope.row.id">
              查看
            </router-link>
          </el-button>
          <el-button
            v-if="scope.row.status === 1"
            type="warning"
            size="mini"
            @click="approvalShow(scope.row)"
          >
            审批
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

    <!-- 审批对话框 -->
    <!-- 为了测试，本页面将查看与审批分为两个按钮，审批按钮只能在状态为审批中时显示 -->
    <!-- 点击按钮，弹出对话框，提交后作为表单传回后端 -->
    <el-dialog title="审批" :visible.sync="dialogVisible" width="490px">
      <el-form label-position="right" label-width="100px">
        <el-form-item label="是否通过">
          <el-radio-group v-model="borrowInfoApproval.status">
            <el-radio :label="2">通过</el-radio>
            <el-radio :label="-1">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="borrowInfoApproval.status == 2" label="标的名称">
          <el-input v-model="borrowInfoApproval.title" />
        </el-form-item>
        <el-form-item v-if="borrowInfoApproval.status == 2" label="起息日">
          <el-date-picker
            v-model="borrowInfoApproval.lendStartDate"
            type="date"
            placeholder="选择开始时间"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item v-if="borrowInfoApproval.status == 2" label="年化收益率">
          <el-input v-model="borrowInfoApproval.lendYearRate">
            <template slot="append">%</template>
          </el-input>
        </el-form-item>
        <el-form-item v-if="borrowInfoApproval.status == 2" label="服务费率">
          <el-input v-model="borrowInfoApproval.serviceRate">
            <template slot="append">%</template>
          </el-input>
        </el-form-item>
        <el-form-item v-if="borrowInfoApproval.status == 2" label="标的描述">
          <el-input v-model="borrowInfoApproval.lendInfo" type="textarea" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="approvalSubmit">
          确定
        </el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import borrowInfoApi from '@/api/core/borrow-info'

export default {
  data() {
    return {
      page: 1,
      total: null,
      size: 10,
      keyword: null,

      borrowInfoList: [],

      borrowInfoApproval: {
        lendYearRate: 0,
        status: 2,
      },
      dialogVisible: false
    }
  },

  created() {
    this.fetchData()
  },

  methods: {
    // 获取数据渲染页面
    fetchData(){
      borrowInfoApi.getListPage(this.page, this.size, this.keyword)
        .then( response => {
          this.borrowInfoList = response.data.listPage
          this.total = response.data.total
        })
    },
    resetData(){
      this.keyword = null
      this.fetchData()
    },

    // 分页组件回调函数
    changeCurrentPage(page){
      this.page = page
      this.fetchData()
    },
    changePageSize(size){
      this.size = size
      this.fetchData()
    },

    // 展示审核弹窗
    approvalShow(row){
      this.dialogVisible = true
      this.borrowInfoApproval.id = row.id
      this.borrowInfoApproval.lendYearRate = row.borrowYearRate * 100
    },
    // 提交审核结果
    approvalSubmit(){
      this.borrowInfoApproval.lendYearRate = this.borrowInfoApproval.lendYearRate / 100
      this.borrowInfoApproval.serviceRate = this.borrowInfoApproval.serviceRate / 100

      borrowInfoApi.approval(this.borrowInfoApproval)
        .then( response => {
          this.dialogVisible = false
          this.$message.success(response.message)
          this.fetchData()
        })
    }
  }
}
</script>

