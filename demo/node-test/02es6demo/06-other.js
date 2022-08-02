// 1.参数的默认值，与解构赋值结合
function connect({ host = '127.0.0.1', username, password, port }) {
    console.log(host)
    console.log(username)
    console.log(password)
    console.log(port)
}

connect({
    username: 'root',
    password: 'root',
    port: 3306
})


// 2.对象扩展运算符, 即对象的拷贝而不是地址值的复制
let person = {
    name: 'sarah',
    age: '1000'
}
let someone = {...person}
someone.age = 100
console.log(person)
console.log(someone)


// 3.箭头函数 lambda表达式
//普通函数
let f1 = function (a){
    return a + 100
}
//箭头函数
let f2 = (a) => {
    return a + 200
}
//简写
let f3 = a => a + 300

console.log(f1(1), f2(1), f3(1))