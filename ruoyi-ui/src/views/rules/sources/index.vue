<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--用户数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
          <el-form-item label="数据源名称" prop="name">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入数据源名称"
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
              v-hasPermi="['rule:source:add']"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="el-icon-edit"
              size="mini"
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['rule:source:edit']"
            >修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['rule:source:remove']"
            >删除</el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="sourceList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="数据源id" align="center" key="id" prop="id" v-if="columns[0].visible" />
          <el-table-column label="名称" align="center" key="name" prop="name" v-if="columns[1].visible" :show-overflow-tooltip="true" />
          <el-table-column label="格式" align="center" key="format" prop="format" v-if="columns[2].visible" :show-overflow-tooltip="true" />
          <el-table-column label="状态" align="center" key="status" v-if="columns[3].visible">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.active"
                @change="handleStatusChange(scope.row)"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns[4].visible" width="160">
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
              >详情</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['rule:source:edit']"
              >修改</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['rule:source:remove']"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-col>
    </el-row>

    <!-- 添加或修改数据源对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="数据源名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入数据源名称" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="数据源描述" prop="desc">
              <el-input v-model="form.desc" placeholder="请输入数据源描述" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="数据源格式" prop="format">
              <el-select v-model="form.format" placeholder="格式">
                <el-option
                  v-for="item in sourceFormats"
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
            <el-form-item label="数据源类型" prop="type">
              <el-select v-model="form.type" placeholder="数据源类型">
                <el-option
                  v-for="item in sourceTypes"
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
            <el-form-item label="server地址" prop="server">
              <el-input v-model="form.server" placeholder="请输入server" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="订阅topic" prop="topic">
              <el-input v-model="form.topic" placeholder="请输入订阅topic" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <template v-if="fieldsCount >0" >
          <el-scrollbar height="400px">
            <template v-for="(newRow, index) in form.fields">
              <el-row :span="24">
                <el-col :span="12">
                  <el-form-item label="字段">
                    <el-input v-model="newRow.name" placeholder="字段名称" @input="changeWord"/>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="字段类型">
                    <el-select v-model="newRow.data_type" placeholder="字段类型" @input="changeWord">
                      <el-option
                        v-for="item in fieldTypes"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </template>
          </el-scrollbar>
        </template>
        <el-row>
          <el-button type="primary" @click="addFields">添加字段</el-button>
        </el-row>

        <template v-if="form.fields && form.fields.length>0">
          <el-row>
            <el-col :span="12">
              <el-form-item label="时间字段" prop="event_time_field">
                <el-select v-model="form.event_time_field" placeholder="时间字段">
                  <el-option
                    v-for="item in form.fields.filter(e=>e.data_type==='long')"
                    :key="item.name"
                    :label="item.name"
                    :value="item.name">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 数据源数据源对话框 -->
    <el-dialog :title="title" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions
        class="margin-top"
        :column="1"
        size="default"
        border
      >
        <el-descriptions-item label="数据源名称">
          {{sourceDetail.name}}
        </el-descriptions-item>
        <el-descriptions-item label="数据源描述">
          {{sourceDetail.data.desc}}
        </el-descriptions-item>
        <el-descriptions-item label="数据源格式">
          {{sourceDetail.format}}
        </el-descriptions-item>
        <el-descriptions-item label="数据源类型">
          {{sourceDetail.data.type}}
        </el-descriptions-item>
        <el-descriptions-item label="server地址">
          {{sourceDetail.data.server}}
        </el-descriptions-item>
        <el-descriptions-item label="订阅topic">
          {{sourceDetail.data.topic}}
        </el-descriptions-item>
        <el-descriptions-item label="时间属性字段">
          {{sourceDetail.data.event_time_field}}
        </el-descriptions-item>
        <template v-for="(item, index) in sourceDetail.data.fields">
          <el-descriptions-item :label="'字段'+index">
            <el-descriptions
              class="margin-top"
              :column="1"
              size="default"
              border
            >
              <el-descriptions-item label="字段名称">{{item.name}}</el-descriptions-item>
              <el-descriptions-item label="字段类型">{{item.data_type}}</el-descriptions-item>
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
import {listSource, changeSourceStatus, updateSource, addSource, delSource} from "@/api/rules/source"
import { getToken } from "@/utils/auth";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "User",
  dicts: ['sys_normal_disable', 'sys_user_sex'],
  components: { Treeselect },
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
        "fields": []
      },
      sourceList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        format: undefined
      },
      // 列信息
      columns: [
        { key: 0, label: `数据源id`, visible: true },
        { key: 1, label: `名称`, visible: true },
        { key: 2, label: `格式`, visible: true },
        { key: 3, label: `状态`, visible: true },
        { key: 4, label: `创建时间`, visible: true }
      ],
      // 表单校验
      rules: {
      },
      sourceDetail: {
        data: {

        }
      },
      sourceFormats: [
        {
          "label": "log",
          "value": "log"
        },
        {
          "label": "json",
          "value": "json"
        },
        {
          "label": "avro",
          "value": "avro"
        }
      ],
      sourceTypes: [
        {
          "label": "kafka",
          "value": "kafka"
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
    }
  },
  watch: {
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      listSource(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.sourceList = response.rows;
          this.total = response.total;
          this.loading = false;
          this.changeWord();
        }
      );
    },
    // 数据源状态修改
    handleStatusChange(row) {
      let text = row.active === true ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '""' + row.name + '"数据源吗？').then(function() {
        return changeSourceStatus(row.id, row.active);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0";
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
      }
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
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
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    showDetail(row) {
      this.reset();
      this.title = "数据源详情";
      this.detailOpen = true;
      this.sourceDetail = {}
      this.sourceDetail["id"] = row.id;
      this.sourceDetail["name"] = row.name;
      this.sourceDetail["format"] = row.format;
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
      this.fieldsCount = this.form.fields.length
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateSource(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            var data = {}
            data["name"] = this.form.name;
            data["format"] = this.form.format;
            data["desc"] = this.form.desc;
            data["type"] = this.form.type;
            data["topic"] = this.form.topic
            data["fields"] = this.form.fields;
            data["server"] = this.form.server;
            data["event_time_field"] = this.form.event_time_field;
            this.form.data = data
            addSource(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const sourceIds = row.id || this.ids;
      this.$modal.confirm('是否确认删除数据源编号为"' + sourceIds + '"的数据项？').then(function() {
        return delSource(sourceIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    addFields() {
      this.fieldsCount++;
      if (!this.form.fields) {
        this.form.fields = []
      }
      this.form.fields.push({id:this.fieldsCount, name: '', data_type: ''})
    },
    changeWord() {
      this.$forceUpdate()
    }
  }
}
</script>
