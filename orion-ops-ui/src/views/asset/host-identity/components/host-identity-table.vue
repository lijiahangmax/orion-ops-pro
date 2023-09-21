<template>
  <!-- 搜索 -->
  <a-card class="general-card table-search-card">
    <a-query-header :model="formModel"
                    label-align="left"
                    @submit="fetchTableData"
                    @reset="fetchTableData">
      <!-- id -->
      <a-form-item field="id" label="id" label-col-flex="50px">
        <a-input-number v-model="formModel.id" placeholder="请输入id" allow-clear />
      </a-form-item>
      <!-- 名称 -->
      <a-form-item field="name" label="名称" label-col-flex="50px">
        <a-input v-model="formModel.name" placeholder="请输入名称" allow-clear />
      </a-form-item>
      <!-- 用户名 -->
      <a-form-item field="username" label="用户名" label-col-flex="50px">
        <a-input v-model="formModel.username" placeholder="请输入用户名" allow-clear />
      </a-form-item>
      <!-- 主机秘钥 -->
      <a-form-item field="keyId" label="主机秘钥" label-col-flex="50px">
        <host-key-selector v-model="formModel.keyId" allow-clear />
      </a-form-item>
    </a-query-header>
  </a-card>
  <!-- 表格 -->
  <a-card class="general-card table-card">
    <template #title>
      <!-- 左侧标题 -->
      <div class="table-title">
        身份列表
      </div>
      <!-- 右侧按钮 -->
      <div class="table-bar-handle">
        <a-space>
          <!-- 新增 -->
          <a-button type="primary"
                    v-permission="['asset:host-identity:create']"
                    @click="emits('openAdd')">
            新增
            <template #icon>
              <icon-plus />
            </template>
          </a-button>
        </a-space>
      </div>
    </template>
    <!-- table -->
    <a-table row-key="id"
             class="table-wrapper-8"
             ref="tableRef"
             label-align="left"
             :loading="loading"
             :columns="columns"
             :data="tableRenderData"
             :pagination="pagination"
             @page-change="(page) => fetchTableData(page, pagination.pageSize)"
             @page-size-change="(size) => fetchTableData(pagination.current, size)"
             :bordered="false">
      <!-- 操作 -->
      <template #keyId="{ record }">
        {{ record.keyName }}
      </template>
      <!-- 操作 -->
      <template #handle="{ record }">
        <div class="table-handle-wrapper">
          <!-- 修改 -->
          <a-button type="text"
                    size="mini"
                    v-permission="['asset:host-identity:update']"
                    @click="emits('openUpdate', record)">
            修改
          </a-button>
          <!-- 删除 -->
          <a-popconfirm content="确认删除这条记录吗?"
                        position="left"
                        type="warning"
                        @ok="deleteRow(record)">
            <a-button v-permission="['asset:host-identity:delete']"
                      type="text"
                      size="mini"
                      status="danger">
              删除
            </a-button>
          </a-popconfirm>
        </div>
      </template>
    </a-table>
  </a-card>
</template>

<script lang="ts">
  export default {
    name: 'asset-host-identity-table'
  };
</script>

<script lang="ts" setup>
  import { reactive, ref } from 'vue';
  import { deleteHostIdentity, getHostIdentityPage, HostIdentityQueryRequest, HostIdentityQueryResponse } from '@/api/asset/host-identity';
  import { Message } from '@arco-design/web-vue';
  import useLoading from '@/hooks/loading';
  import columns from '../types/table.columns';
  import { defaultPagination } from '@/types/table';
  import { getHostKeyList } from '@/api/asset/host-key';
  import { useCacheStore } from '@/store';
  import HostKeySelector from '@/components/asset/host-key/host-key-selector.vue';

  const tableRenderData = ref<HostIdentityQueryResponse[]>();
  const { loading, setLoading } = useLoading();
  const emits = defineEmits(['openAdd', 'openUpdate']);

  const cacheStore = useCacheStore();
  const pagination = reactive(defaultPagination());

  const formModel = reactive<HostIdentityQueryRequest>({
    id: undefined,
    name: undefined,
    username: undefined,
    keyId: undefined,
  });

  // 删除当前行
  const deleteRow = async ({ id }: { id: number }) => {
    try {
      setLoading(true);
      // 调用删除接口
      await deleteHostIdentity(id);
      Message.success('删除成功');
      // 重新加载数据
      await fetchTableData();
    } finally {
      setLoading(false);
    }
  };

  // 添加后回调
  const addedCallback = () => {
    fetchTableData();
  };

  // 更新后回调
  const updatedCallback = () => {
    fetchTableData();
  };

  defineExpose({
    addedCallback, updatedCallback
  });

  // 加载数据
  const doFetchTableData = async (request: HostIdentityQueryRequest) => {
    try {
      setLoading(true);
      const { data } = await getHostIdentityPage(request);
      tableRenderData.value = data.rows;
      pagination.total = data.total;
      pagination.current = request.page;
      pagination.pageSize = request.limit;
    } finally {
      setLoading(false);
    }
  };

  // 切换页码
  const fetchTableData = (page = 1, limit = pagination.pageSize, form = formModel) => {
    doFetchTableData({ page, limit, ...form });
  };
  fetchTableData();

  // 获取主机秘钥列表
  const fetchHostKeyList = async () => {
    const { data } = await getHostKeyList();
    cacheStore.set('hostKeys', data);
  };
  fetchHostKeyList();

</script>

<style lang="less" scoped>

</style>