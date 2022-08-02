// 一种构造函数，用于封装异步操作，从它可以获取异步操作的消息
const fs = require('fs')

//实例化Promise对象，三种状态：初始化pending、成功resolved、失败rejected
const p = new Promise((resolve, reject) => {
    fs.readFile('../01nodedemo/他.txt', (err,data) => {
        if (err) reject(err)
        resolve(data)
    })
})

//调用 promise 对象的方法
//then：当Promise状态 成功 时执行
//catch：当Promise状态 失败 时执行
p.then(response => {
    console.log(response.toString())
}).catch(error => {
    console.log('出错了')
    console.error(error)
})