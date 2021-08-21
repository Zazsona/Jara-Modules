package com.zazsona.countdownnumbers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class ShuntingYard
{
    public static Stack<String> getPostFixNotation(String equation)
    {
        String[] elements = getEquationElements(equation);
        HashMap<String, Integer> precedenceMap = getPrecedenceMap();
        HashMap<String, Boolean> leftAssociatedMap = getAssociationMap();
        Stack<String> output = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (int i = 0; i<elements.length; i++)
        {
            if (Pattern.matches("[0-9]+", elements[i]))
            {
                output.push(elements[i]);
            }
            else if (elements[i].equals("+") || elements[i].equals("-") || elements[i].equals("/") || elements[i].equals("*") || elements[i].equals("^"))
            {
                while (!operators.empty() && leftAssociatedMap.get(elements[i]) && precedenceMap.get(elements[i]) <= precedenceMap.get(operators.peek())) //If our new operator is lower priority, empty the stack until it is equal or higher priority
                {
                    output.push(operators.pop());
                }
                operators.push(elements[i]);
            }
            else if (elements[i].equals("("))
            {
                operators.push("(");
            }
            else if (elements[i].equals(")"))
            {
                while (!operators.peek().equals("(")) //TODO: Handle if this isn't there, or operators is empty (Hint: malformed expression)
                {
                    output.push(operators.pop());
                }
                operators.pop(); //Discard bracket.
            }
        }
        while (!operators.empty())
        {
            output.push(operators.pop());
        }
        return reverseStack(output);
    }

    private static HashMap<String, Integer> getPrecedenceMap()
    {
        HashMap<String, Integer> precedenceMap = new HashMap<>();
        precedenceMap.put("(", Integer.MIN_VALUE);
        precedenceMap.put("-", 2);
        precedenceMap.put("+", 2);
        precedenceMap.put("*", 3);
        precedenceMap.put("/", 3);
        precedenceMap.put("^", 4);
        return precedenceMap;
    }

    private static HashMap<String, Boolean> getAssociationMap()
    {
        HashMap<String, Boolean> associationMap = new HashMap<>();
        associationMap.put("(", true);
        associationMap.put("-", true);
        associationMap.put("+", true);
        associationMap.put("*", true);
        associationMap.put("/", true);
        associationMap.put("^", false);
        return associationMap;
    }

    private static String[] getEquationElements(String equation)
    {
        equation = equation.replace(" ", "");

        equation = equation.replace("+", " + ");
        equation = equation.replace("-", " - ");
        equation = equation.replace("*", " * ");
        equation = equation.replace("/", " / ");
        equation = equation.replace("^", " ^ ");
        equation = equation.replace("(", "( ");
        equation = equation.replace(")", " )");

        String[] result = solveNegativeNumbers(equation.split(" "));
        return result;
    }
    private static String[] solveNegativeNumbers(String[] equation)
    {
        ArrayList<String> newEquation = new ArrayList<>();
        boolean wasLastIndexOperator = true;
        boolean addBracket = false;
        for (int i = 0; i<equation.length; i++)
        {
            if (wasLastIndexOperator && equation[i].equals("-"))
            {
                newEquation.add("(");
                newEquation.add("0");
                addBracket = true;
            }
            newEquation.add(equation[i]);
            if (addBracket && Pattern.matches("[0-9]+", equation[i]))
            {
                newEquation.add(")");
                addBracket = false;
            }
            wasLastIndexOperator = !Pattern.matches("[0-9]+", equation[i]);
        }
        return newEquation.toArray(new String[0]);
    }

    private static Stack<String> reverseStack(Stack<String> stack)
    {
        Stack<String> reversedStack = new Stack<>();
        while (!stack.isEmpty())
        {
            reversedStack.push(stack.pop());
        }
        return reversedStack;
    }
}
