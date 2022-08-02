// 在定义了变量后的对象简写
let username = 'Luda'
let age = 25
let sing = function (){
    console.log("WJ Stay?")
}

let idol = {
    username,
    age,
    sing
}
console.log(idol)
idol.sing()
console.log(idol.sing)

// 对象中的方法简写，属性名默认为方法名
let person = {
    sayHi() {
        console.log("Hello!")
    }
}
person.sayHi()
