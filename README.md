# Reactor VS Servlet

服务介绍
* servlet: 端口8081,提供servlet服务
* servlet-1: 端口8082，提供servlet服务，用于测试服务间请求的测试
* reactor: 端口8083，提供reactor服务

# 测试对比
测试脚本
wrk -c 1000 -t 1000 -d 10 --latency 
测试场景
- 简单业务：只有包含DB操作
- 自身阻塞：服务耗时在自身服务上
- 服务阻塞：耗时花在服务间请求上
- 文件上传：文件上传业务
- 
吞吐量表

单位：Requests/sec

| 业务类型 |  servlet |  reactor   | 描述                                                 |
|------|---------|-----|----------------------------------------------------|
| 简单业务 |    2077     |  2174   | 差别不大                                               |
| 自身阻塞 |    4556     |  735   | servlet远高于reactor，主要是因为servlet是大线程池                |
| 服务阻塞 |    1840     |  4533   | reactor通过响应式的方式，避免了线程阻塞，提高了吞吐。而servlet线程池大小决定了并发上限 |
| 文件上传 |      13   |   13  | 主要瓶颈在磁盘和网络条件，不在调度。                                 |

# 总结
1. 调用其他短耗时请求，servlet与reactor差别不大。
2. 如果是阻塞时间在自身服务，那么大线程池更具备优势，也就是servlet吞吐更高。
3. 为了避免其他业务的耗时影响的自身业务的吞吐，则应该使用reactor。
4. 文件上传等大数据量传输场景下，瓶颈一般在于硬件条件，而不在IO调度。