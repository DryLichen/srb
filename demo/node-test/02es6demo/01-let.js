// 1.可以重复var, 但不能重复let
var a = 1
var a = 2
console.info(a)
let b = 'Helen'
b = 3
console.info(b)

// 2.let存在块级作用域, var只存在函数作用域
// if else while for
let flag = true
if(flag) {
    // let star = 5
    var star = 5
}
console.info(star)

// 3.变量提升
// var定义的变量在全局都能拿到，所以不报错而是 undefined；但let不能提升
console.info(username)
// var username
let username