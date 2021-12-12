# Gradle EasyHook 插件Demo

## 本例通过Gradle Transform API + Javassists实现动态注入或修改代码。
#### 优点:

1.  集成简单.
2. 功能强大。支持正则匹配 + 插入代码(执行前后) + 替换方法执行 + 修改参数.

#### 集成步骤(仓库配置自定义):
#### 1).在各个需要生成的module分别引入hook库插件
```groovy
plugins {
    id "xmq-lifecycle"
}
```


#### 2).在build.gradle中配置hook点
```groovy
/** 配置hook点 */
hookGo {
    addHooker("com.xmq.codeline.MockLog.d\\([a-zA-Z;/]+\\)V") {
        // 在源代码前注入
        it.insertBefore("System.out.println(\"insertBefore MockLog.d\");")
    }
    addHooker("com.xmq.codeline.MockLog.d\\([a-zA-Z;/]+\\)V") {
        // 在源代码后注入
        it.insertAfter("System.out.println(\"insertAfter MockLog.d\");")
    }
    addHooker("com.xmq.codeline.TestUtil.onCreate\\(\\)V") {
        // 替换源代码执行
        it.replace("System.out.println(\"Invoke TestUtil.onCreate()\");")
    }
    addHooker("*.MainActivity.test\\(*\\)*") {
        // 替换执行参数
        it.replaceParameter(1, "\"replace Parameter from \"+\$1 ;")
    }
    addHooker("com.xmq.codeline.MockLog.*\\(*\\)V") {
        // 修改第一个参数
        it.calcParameter(1, "\"(\${fileName}:\${line})\" + \$1;")
    }
}
```



