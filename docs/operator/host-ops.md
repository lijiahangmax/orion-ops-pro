### 主机终端

主机终端页面 支持 SSH, SFTP。  
打开后默认会进入新建连接页面, 页面的主机数据是用户授权的资产数据。  
鼠标移入列表内的主机上时, 右侧会出现 `打开 SSH` `打开 SFTP` `主机设置` `收藏` 的按钮。

> 主机设置

* SSH 配置: 可以自定义配置连接主机的 密码/秘钥/身份, 仅对自己生效, 不会修改全局配置, `秘钥` `身份` 数据是用户授权的资产数据
* 标签颜色: 自定义配置标签的颜色, 可以用来区分环境等

> 顶部状态栏

* 全屏: 开启或关闭全屏

> 左侧状态栏

* 新建连接: 新建主机连接 `SSH` `SFTP`
* 快捷键设置: 配置 `全局` `会话` `终端` 的快捷键
* 显示设置: 配置终端 `显示偏好` `操作栏按钮` `右键菜单` 设置
* 主题设置: 修改终端主题配色
* 终端设置: 配置终端 `交互` `插件` `会话` 设置

> 右侧状态栏

* 命令片段: 自定义快速执行的命令片段, 双击直接执行
* 传输列表: 打开文件传输列表, 当前会话下, 所有的文件上传下载传输都会显示在这里
* 截图: 截屏终端并且自动下载

> 文件传输

点击上传或者下载后会自动添加到传输列表。

* 上传: 关闭页面自动清除
* 下载: 下载完成后自动下载, 关闭页面自动清除

> SFTP

* 预览: 默认只能预览 2MB 以内的普通文件, 这个大小可以在前端 env 文件中修改 `VITE_SFTP_PREVIEW_MB`

> 终端面板

⭐ 双击终端标签可快速复制会话

### 执行模板

用来维护批量执行的命令模板, 支持动态参数, 使用 `@{{ xxx }}` 来替换命令参数。

* 新增: 新增执行模板
* 执行: 使用此命令模板批量执行主机命令
* 修改: 修改执行模板
* 删除: 删除执行模板

> 内置参数

| 参数              | 描述                       |
|:----------------|:-------------------------|
| execId          | 执行记录id                   |
| hostId          | 执行主机id                   | 
| hostName        | 执行主机名称                   |
| hostCode        | 执行主机编码                   |
| hostAddress     | 执行主机地址                   |
| userId          | 执行用户id                   |
| username        | 执行用户名                    |
| uuid            | 生成任务维度 uuid              |
| uuidShort       | 生成任务维度 uuid 无 '-'        |
| hostUuid        | 生成机器维度 uuid              |
| hostUuidShort   | 生成机器维度 uuid 无 '-'        |
| timestampMillis | 时间戳毫秒                    |
| timestamp       | 时间戳                      |
| date            | 执行时间 yyyy-MM-dd          |
| datetime        | 执行时间 yyyy-MM-dd HH:mm:ss |
