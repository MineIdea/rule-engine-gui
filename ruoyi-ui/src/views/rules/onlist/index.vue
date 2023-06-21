<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--模型数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
                 label-width="90px">
          <el-form-item label="模型id" prop="id">
            <el-input
              v-model="queryParams.id"
              placeholder="请输入模型id"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="模型名称" prop="name">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入模型名称"
              clearable
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>

          <el-form-item :gutter="20">
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
              v-hasPermi="['rule:model:add']"
            >新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['rule:model:remove']"
            >删除
            </el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getRuleList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="ruleList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column label="模型id" align="center" key="id" prop="id" v-if="columns[0].visible"/>
          <el-table-column label="模型名称" align="center" key="name" prop="name" v-if="columns[1].visible"
                           :show-overflow-tooltip="true"/>
          <el-table-column label="状态" align="center" key="status" v-if="columns[2].visible">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.active"
                @change="handleStatusChange(scope.row)"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[3].visible" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            align="center"
            width="160"
            class-name="small-padding fixed-width"
          >
            <template slot-scope="scope" v-if="scope.row.userId !== 1">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="showDetail(scope.row)"
                v-hasPermi="['rule:source:list']"
              >详情
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['rule:source:edit']"
              >修改
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['rule:source:remove']"
              >删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getRuleList"
        />
      </el-col>
    </el-row>

    <!-- 添加或修改规则对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="模型名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入模型名称" maxlength="30"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="模型描述" prop="desc">
              <el-input v-model="form.desc" placeholder="请输入模型描述" maxlength="30"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="模型类型" prop="format">
              <el-select v-model="form.type" placeholder="类型">
                <el-option
                  v-for="item in modelType"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="威胁等级" prop="type">
              <el-select v-model="form.risk_level" placeholder="威胁等级">
                <el-option
                  v-for="item in riskLevels"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <template v-if="form.rules != null && form.rules.length >0">
          <template v-for="(newRule, index) in form.rules">
            <el-row>
              <el-col>
                规则{{index}}
              </el-col>
              <el-col>
                <el-scrollbar height="400px">
                  <el-row :span="24">
                    <el-col :span="12">
                      <el-form-item label="规则名称">
                        <el-input v-model="newRule.name" placeholder="规则名称" @input="changeWord"/>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col :span="12">
                      <el-form-item label="描述">
                        <el-input v-model="newRule.desc" placeholder="描述" @input="changeWord"/>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col>
                      <el-form-item label="规则类型">
                        <el-select v-model="newRule.type" placeholder="规则类型" @input="changeWord">
                          <el-option
                            v-for="item in ruleType"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                          </el-option>
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-col>
                      <el-form-item label="数据源">
                        <el-select v-model="newRule.source" placeholder="数据源" @input="handleSelectedSource(newRule, index)">
                          <el-option
                            v-for="item in sourceList"
                            :key="item.name"
                            :label="item.name"
                            :value="item.name">
                          </el-option>
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <template v-if="newRule.filters != null && newRule.filters.length>0">
                    <el-scrollbar height="400px">
                      <template v-for="(filterItem, filterIndex) in newRule.filters">
                        <el-row>
                          <el-col>过滤条件{{filterItem.fid}}</el-col>
                        </el-row>
                        <el-row>
                          <el-form-item label="fid">
                            <el-col :span="10">
                              <el-input v-model="filterItem.fid" disabled placeholder="fid" @input="changeWord"/>
                            </el-col>
                          </el-form-item>
                          <el-col>
                            <el-form-item label="字段">
                              <el-select v-model="filterItem.name" @input="handlerFilterName(filterItem, newRule)">
                                <el-option
                                  v-for="item in newRule.selectedSource.data.fields"
                                  :key="item.name"
                                  :value="item.name">
                                </el-option>
                              </el-select>
                            </el-form-item>
                          </el-col>
                          <template v-if="filterItem.name != null && filterItem.name !== ''">
                            <el-col>
                              <el-form-item label="操作符">
                                <el-select v-model="filterItem.op" @input="changeWord">
                                  <el-option
                                    v-for="item in getScalarOp(filterItem.data_type)"
                                    :key="item"
                                    :value="item">
                                  </el-option>
                                </el-select>
                              </el-form-item>
                            </el-col>
                          </template>
                          <template v-if="filterItem.op != null && filterItem.op !== '' && needOpValue(filterItem)">
                            <el-form-item label="值">
                              <el-col :span="10">
                              <el-input v-model="filterItem.value" placeholder="值" @input="changeWord"/>
                              </el-col>
                            </el-form-item>
                          </template>
                        </el-row>
                      </template>
                    </el-scrollbar>
                  </template>
                  <template v-if="newRule.source !== undefined && newRule.source !== ''">
                    <el-row  style="margin-bottom: 10px">
                      <el-button type="primary" @click="addFilter(newRule)">添加过滤条件</el-button>
                    </el-row>
                  </template>
                  <template v-if="newRule.filters.length && newRule.filters.length>0">
                    <el-row >
                      <el-col :span="4">过滤器模式</el-col>
                      <el-col :span="20">
                        <el-row v-for="(pattern, patternIndex) in newRule.filter_patterns">
                          <el-col :span="20">
                            <el-checkbox-group v-model="newRule.filter_patterns[patternIndex]">
                              <el-checkbox-button
                                v-for="filter in newRule.filters"
                                :key="filter.fid"
                                :label="filter.fid"
                                @change="changeWord"
                              >{{ filter.fid }}</el-checkbox-button>
                            </el-checkbox-group>
                          </el-col>
                          <el-col :span="4">
                            <el-button v-if="newRule.filters.length && newRule.filters.length>0" circle @click="addPattern(newRule, patternIndex)">or</el-button>
                          </el-col>
                        </el-row>
                      </el-col>
                    </el-row>
                  </template>
                </el-scrollbar>
              </el-col>
            </el-row>
            <el-divider />
          </template>
        </template>

        <el-row>
          <el-button type="primary" @click="addRules">添加规则</el-button>
        </el-row>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 规则详情对话框 -->
    <el-dialog :title="title" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions
        class="margin-top"
        :column="1"
        size="default"
        border
      >
        <el-descriptions-item label="模型名称">
          {{ sourceDetail.name }}
        </el-descriptions-item>
        <el-descriptions-item label="模型描述">
          {{ sourceDetail.data.desc }}
        </el-descriptions-item>
        <el-descriptions-item label="模型类型">
          {{ sourceDetail.data.type }}
        </el-descriptions-item>
        <template v-for="(item, index) in sourceDetail.data.rules">
          <el-descriptions-item :label="'规则'+index">
            <el-descriptions
              class="margin-top"
              :column="1"
              size="default"
              border
            >
              <el-descriptions-item label="规则名称">{{ item.name }}</el-descriptions-item>
              <el-descriptions-item label="规则描述">{{ item.desc }}</el-descriptions-item>
              <el-descriptions-item label="规则类型">{{ item.type }}</el-descriptions-item>
              <el-descriptions-item label="规则数据源">{{ item.source }}</el-descriptions-item>
              <el-descriptions-item label="过滤条件">
                <el-descriptions
                  class="margin-top"
                  :column="1"
                  size="default"
                  border
                >
                  <template v-for="(filter, filterIndex) in item.filters">
                    <el-descriptions-item label="过滤器id">{{ filter.fid }}</el-descriptions-item>
                    <el-descriptions-item label="过滤器字段">{{ filter.name }}</el-descriptions-item>
                    <el-descriptions-item label="过滤器操作符">{{ filter.op }}</el-descriptions-item>
                    <el-descriptions-item v-if="!!filter.value" label="操作值">{{ filter.value }}</el-descriptions-item>
                  </template>
                </el-descriptions>
              </el-descriptions-item>
              <el-descriptions-item label="过滤器条件模式组合">
                <template v-for="(pattern, patternIndex) in item.filter_patterns">
                  <div>{{pattern}}</div>
                </template>
              </el-descriptions-item>

            </el-descriptions>
          </el-descriptions-item>
        </template>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<style>
.scrollbar-demo-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  margin: 10px;
  text-align: center;
  border-radius: 4px;
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.el-descriptions {
  margin-top: 20px;
}

.cell-item {
  display: flex;
  align-items: center;
}

.margin-top {
  margin-top: 20px;
}
</style>

<script>
import {listSource, changeSourceStatus, updateSource, addSource,} from "@/api/rules/source"
import {listRule, changeModelStatus, updateRule, addRule} from "@/api/rules/rule"
import {getToken} from "@/utils/auth";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import function_info from "@/api/rules/function_info.json"

export default {
  name: "User",
  dicts: ['sys_normal_disable', 'sys_user_sex'],
  components: {Treeselect},
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      detailOpen: false,
      fieldsCount: 0,
      // 表单参数
      form: {
        "rules": []
      },
      sourceList: [],
      ruleList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        id: undefined,
        name: undefined,
      },
      // 列信息
      columns: [
        {key: 0, label: `模型id`, visible: true},
        {key: 1, label: `模型名称`, visible: true},
        {key: 2, label: `状态`, visible: true},
        {key: 3, label: `创建时间`, visible: true}
      ],
      // 表单校验
      rules: {},
      sourceDetail: {
        data: {}
      },
      modelType: [
        {
          "label": "阈值",
          "value": "threshold"
        }
      ],
      ruleType: [
        {
          "label": "hit",
          "value": "hit"
        },
        {
          "label": "agg",
          "value": "agg"
        }
      ],
      riskLevels: [
        {
          "label": "info",
          "value": "info"
        },
        {
          "label": "low",
          "value": "low"
        },
        {
          "label": "middle",
          "value": "middle"
        },
        {
          "label": "high",
          "value": "high"
        },
        {
          "label": "critical",
          "value": "critical"
        }
      ],
      fieldTypes: [
        {
          "label": "string",
          "value": "string"
        },
        {
          "label": "int",
          "value": "int"
        },
        {
          "label": "long",
          "value": "long"
        },
        {
          "label": "float",
          "value": "float"
        },
        {
          "label": "double",
          "value": "double"
        },
        {
          "label": "boolean",
          "value": "boolean"
        },
        {
          "label": "list",
          "value": "list"
        },
        {
          "label": "map",
          "value": "map"
        },
        {
          "label": "object",
          "value": "object"
        }
      ],
      function_info: function_info
    }
  },
  watch: {},
  created() {
    this.getSourceList();
    this.getRuleList();
  },
  methods: {
    /** 查询规则 */
    getRuleList() {
      this.loading = true;
      listRule(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.ruleList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 查询模型列表 */
    getSourceList() {
      this.loading = true;
      listSource(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.sourceList = response.rows;
          this.loading = false;
        }
      );
    },
    // 数据源状态修改
    handleStatusChange(row) {
      let text = row.active === true ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '""' + row.name + '"数据源吗？').then(function () {
        return changeModelStatus(row.id, row.active);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function () {
        row.active = row.active === "0" ? "1" : "0";
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {}
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getRuleList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.$refs.tree.setCurrentKey(null);
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    showDetail(row) {
      this.reset();
      this.title = "模型详情";
      this.detailOpen = true;
      this.sourceDetail = {}
      this.sourceDetail["id"] = row.id;
      this.sourceDetail["name"] = row.name;
      this.sourceDetail["active"] = row.active;
      this.sourceDetail["data"] = row.data;
    },
    /** 新增按钮操作 */
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;

      // getSource().then(response => {
      //   this.postOptions = response.posts;
      //   this.roleOptions = response.roles;
      //   this.title = "添加用户";
      //   this.form.password = this.initPassword;
      // });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.open = true;
      this.title = "修改数据源";
      var data = this.sourceList.find(item => item.id === row.id).data;
      this.form.name = data.name
      this.form.format = data.format
      this.form.desc = data.desc
      this.form.type = data.type
      this.form.topic = data.topic
      this.form.fields = data.fields
      this.form.id = row.id
      this.fieldsCount = this.form.fields.length || 0
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateSource(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getSourceList();
            });
          } else {
            var data = {}
            data["name"] = this.form.name;
            data["format"] = this.form.format;
            data["desc"] = this.form.desc;
            data["type"] = this.form.type;
            data["topic"] = this.form.topic
            data["fields"] = this.form.fields;
            this.form.data = data
            addSource(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getSourceList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const userIds = row.userId || this.ids;
      this.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(function () {
        return delSource(userIds);
      }).then(() => {
        this.getSourceList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    addRules() {
      this.fieldsCount++;
      if (!this.form.rules) {
        this.form.rules = []
      }
      this.form.rules.push({filters:[], filter_patterns:[[]]})
      this.changeWord()
    },
    handleSelectedSource(rule, index) {
      rule.selectedSource = this.sourceList.find(e=>e.name===rule.source)
      this.changeWord()
    },
    changeWord() {
      this.$forceUpdate()
    },
    addFilter(rule) {
      if (!rule.filters) {
        rule.filters = []
      }
      rule.filters.push({fid: rule.filters.length})
      this.changeWord()
    },
    handlerFilterName(filter, rule) {
      filter.data_type = rule.selectedSource.data.fields.find(e=>e.name===filter.name).data_type
      this.changeWord()
    },
    addPattern(rule, index) {
      rule.filter_patterns[index+1] = []
      this.changeWord()
    },
    getScalarOp(data_type) {
      const res = [];
      if (data_type !== 'object') {
        Object.keys(this.function_info.scalar.object).forEach(e=>res.push(e))
      }
      Object.keys(this.function_info.scalar[data_type]).forEach(e=>res.push(e))
      return res.sort()
    },
    needOpValue(filter) {
      let op_detail = this.function_info.scalar[filter.data_type][filter.op]
      if (op_detail === undefined) {
        op_detail = this.function_info.scalar.object[filter.op]
      }
      if (op_detail.type === 'unary') {
        return false;
      }
      return true;
    }
  }
}
</script>
