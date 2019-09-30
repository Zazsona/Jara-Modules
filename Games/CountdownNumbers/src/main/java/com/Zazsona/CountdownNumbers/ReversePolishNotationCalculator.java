package com.Zazsona.CountdownNumbers;

import java.util.Stack;

public class ReversePolishNotationCalculator
{
    public static double calculate(Stack<String> postFixNotation)
    {
        double number1 = 0;
        double number2 = 0;
        Stack<Double> rpmStack = new Stack<>();

        while (!postFixNotation.empty())
        {
            String token = postFixNotation.pop();
            if(token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^"))
            {
                switch (token)
                {
                    case "+":
                        number2 = rpmStack.pop();
                        number1 = rpmStack.pop();
                        rpmStack.push(number1+number2);
                        break;
                    case "-":
                        number2 = rpmStack.pop();
                        number1 = rpmStack.pop();
                        rpmStack.push(number1-number2);
                        break;
                    case "/":
                        number2 = rpmStack.pop();
                        number1 = rpmStack.pop();
                        rpmStack.push(number1/number2);
                        break;
                    case "*":
                        number2 = rpmStack.pop();
                        number1 = rpmStack.pop();
                        rpmStack.push(number1*number2);
                        break;
                    case "^":
                        number2 = rpmStack.pop();
                        number1 = rpmStack.pop();
                        rpmStack.push(Math.pow(number1, number2));
                        break;
                }
            }
            else
            {
                rpmStack.push(Double.parseDouble(token));
            }
        }
        while (rpmStack.size() != 1)
        {
            number2 = rpmStack.pop();
            number1 = rpmStack.pop();
            rpmStack.push(number1+number2);
        }
        return rpmStack.pop();
    }
}
