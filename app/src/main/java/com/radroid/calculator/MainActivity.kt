package com.radroid.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.radroid.calculator.databinding.ActivityMainBinding
//import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private var canAddOperation=false
    private var canAddDecimal=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
    }
    @Suppress("UNUSED_PARAMETER")
    fun allClearAction(view: View) {
        binding.apply {
            workingTV.text=""
            resulTV.text=""
        }

    }

    @Suppress("UNUSED_PARAMETER")
    fun backSpaceAction(view: View) {
        val length=binding.workingTV.length()
        if(length>0)
            binding.workingTV.text=binding.workingTV.text.subSequence(0,length-1)
    }


    fun numberAction(view: View){
        if (view is Button){
            if (view.text=="."){
                if(canAddDecimal)
                    binding.workingTV.append(view.text)

                canAddDecimal=false


            }
            binding.workingTV.append(view.text)
            canAddOperation=true
        }
    }


    fun operationAction(view: View){
        if (view is Button && canAddOperation){
            binding.workingTV.append(view.text)
            canAddOperation=false
            canAddDecimal=true
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun equalsAction(view:View) {
        binding.apply {
            resulTV.text = calculateResults()
        }
    }

    private fun calculateResults(): String {
        val digitsOperator=digitsOperators()
        if (digitsOperator.isEmpty())return ""

        val timesDivision=timesDivisionCalculate(digitsOperator)
        if (timesDivision.isEmpty())return ""

        val result=addSubtractCalculate(timesDivision)
        return result.toString()

    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result=passedList[0] as Float

        for (i in passedList.indices){
            if (passedList[i] is Char && i!=passedList.lastIndex){
                val operator=passedList[i]
                val nextDigit=passedList[i+1] as Float
                if (operator=='+')
                    result+=nextDigit
                if (operator=='-')
                    result+=nextDigit
            }
        }

        return result

    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list=passedList
        while (list.contains('X') ||list.contains('/')){
            list=calcTimesDiv(list)
        }
        return list

    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList= mutableListOf<Any>()
        var restartIndex=passedList.size
        for (i in passedList.indices){
            if (passedList[i] is Char && i!=passedList.lastIndex && i<restartIndex){
                val operator = passedList[i]
                val prevDigit=passedList[i-1] as Float
                val nextDigit=passedList[i+1] as Float
                when(operator){
                    'X' ->{
                        newList.add(prevDigit * nextDigit)
                        restartIndex=i+1
                    }
                    '/' ->{
                        newList.add(prevDigit / nextDigit)
                        restartIndex=i+1
                    }
                   else->
                   {
                       newList.add(prevDigit)
                       newList.add(operator)
                   }
                }
            }
            if (i>restartIndex)
                newList.add(passedList[i])
        }
        return newList

    }

    private fun digitsOperators():MutableList<Any>{
        val list= mutableListOf<Any>()
        var currentDigit=""
        for (character in binding.workingTV.text){
            if(character.isDigit()||character=='.')
                currentDigit+=character
            else{
                list.add(currentDigit.toFloat())
                currentDigit=""
                list.add(character)
            }
        }
        if (currentDigit!="")
            list.add(currentDigit.toFloat())
        return list
    }
}