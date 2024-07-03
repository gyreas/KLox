package aadesaed.gyreas.tool

import java.io.PrintWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("usage: generate_ast <output dir>")
        exitProcess(64)
    }
    val outputDir = args[0]
    defineAst(
        outputDir, "Expr", arrayOf(
            "Binary   / val left: Expr, val operator: Token, val right: Expr",
            "Grouping / val expression: Expr ",
            "Literal  / val value: Any?",
            "Unary    / val operator: Token, val right: Expr",
        )
    )
}

fun defineAst(outputDir: String, baseName: String, types: Array<String>) {
    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path, "UTF-8")
    writer.println(
        """
        /* This file was generated by klox/generateAst.kt.
         *  
         * Execute the main in that file to regenerate it
         * 
         * NOTE: This file must exist in the repo
         */
           
        package aadesaed.gyreas.klox
        
        import Token
        
        abstract class $baseName {
            abstract fun <R> accept(visitor: Visitor<R>): R
        }
        
    """.trimIndent()
    )

    types.forEach { type ->
        val typeSplit = type.split("/")
        val className = typeSplit[0].trim()
        val fields = typeSplit[1].trim()
        defineType(writer, baseName, className, fields)
    }

    defineVisitor(writer, baseName, types)

    writer.close()
}

fun defineType(writer: PrintWriter, baseName: String, className: String, fields: String) {
    writer.println(
        """
        data class $className($fields) : $baseName() {
            override fun <R> accept(visitor: Visitor<R>): R {
                return visitor.visit$className$baseName(this)
            }
        } 
        
        """.trimIndent()
    )
}

fun defineVisitor(writer: PrintWriter, baseName: String, types: Array<String>) {
    writer.println("interface Visitor<R> {")
    types.forEach { type ->
        val typeName = type.split("/")[0].trim()
        writer.println("    fun visit$typeName$baseName(${baseName.lowercase()}: $typeName): R")
    }
    writer.println("}")
}