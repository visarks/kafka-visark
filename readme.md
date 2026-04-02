<h1 align="center">kafka-visark</h1>
<h4 align="center">跨平台 Kafka 客户端 · 简洁高效</h4>
<p align="center">
    <a><img src='https://img.shields.io/badge/Java-26-green' alt='Java'></img></a>
    <a><img src='https://img.shields.io/badge/JavaFX-27-green' alt='JavaFX'></img></a>
    <a><img src='https://img.shields.io/badge/Kafka-4.2.0-orange' alt='Kafka'></img></a>
    <a><img src='https://img.shields.io/badge/License-MIT-green' alt='License'></img></a>
</p>

<p align="center">
    <a title="GitHub Stars"><img src='https://img.shields.io/github/stars/visarks/kafka-visark?color=green&label=Stars' alt='Stars'/></a>
    <a title="GitHub Forks"><img src='https://img.shields.io/github/forks/visarks/kafka-visark?color=green&label=Forks' alt='Forks'/></a>
    <a title="Gitee Stars"><img src='https://gitee.com/podigua/kafka-visark/badge/star.svg?theme=dark' alt='Gitee Star'/></a>
    <a title="Gitee Forks"><img src='https://gitee.com/podigua/kafka-visark/badge/fork.svg?theme=dark' alt='Gitee Fork'/></a>
</p>

---

## 简介

kafka-visark 是一款基于 JavaFX 开发的跨平台 Kafka 客户端工具，提供直观的图形化界面，让 Kafka 的管理和操作更加简单高效。

支持 Windows、macOS、Linux 多平台，无需安装，开箱即用。

## 功能特性

### 集群管理
- 多集群连接管理，支持分组整理
- 支持 SASL/PLAIN、SASL/SCRAM 等认证机制
- 实时查看 Broker 节点信息

### Topic 管理
- Topic 创建、删除、查看
- Partition 数量调整
- Topic 配置参数设置

### 消息操作
- 消息发送（支持 JSON、String、Base64、Hex 等格式）
- 消息消费与实时订阅
- 多维度消息查询：
  - 按时间范围查询
  - 按 Offset 查询
  - 按 Partition 查询

### Consumer 管理
- 消费组列表查看
- 消费组成员详情
- 消费进度（Lag）监控
- 消费组删除

### 其他特性
- 深色/浅色主题切换
- 多语言支持
- 自动更新检测


## 安装使用

### 下载
前往  [GitHub Releases](https://github.com/visarks/kafka-visark/releases) 下载对应平台的安装包。

### 运行要求
- Java 26+（打包版本已内置 JRE，无需额外安装）


## 贡献与支持

欢迎参与项目贡献：

1. 在 GitHub/Gitee 上 Star 项目
2. 提交 Issue 反馈问题或建议
3. 提交 PR 参与代码贡献
4. 与朋友同事分享 kafka-visark

## 捐赠支持

如果这个项目对您有帮助，欢迎捐赠支持开发者：

![微信](doc/weixin.png)

## 官网

https://www.visark.cn/

## License

MIT License