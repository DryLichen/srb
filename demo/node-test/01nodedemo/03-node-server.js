//引入Node.js的http模块:
const http = require('http')

const hostname = '127.0.0.1'
const port = 8888

//调用createServer创建服务器
const server = http.createServer(
    (request, response) => {
        /**
         * 发送 HTTP 头部
         * HTTP 状态值: 200 : OK
         * 内容类型: text/plain
         */
        // response.writeHead(200, {'Content-Type': 'text/html'});
        response.setHeader('Content-Type', 'text/html')
        response.statusCode = 200

        // 发送响应数据
        response.end('<h1> Hello Node.js Server</h1>')
    }
)

server.listen(port, hostname)

//终端打印信息
console.log(`Server is running at http://${hostname}:${port}/`)

