#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include"closure_goto.h"
#include"parsingtable.h"
#include"first_follow.h"
#include"parse.h"

void lr0_parser();

void operator_precedence_parser();
char *input;
int i = 0;
char lasthandle[6],inputstack[50], handles[][5] = {")E(", "E*E", "E+E", "E-E", "E*E", "E/E", "E^E", "i"};
// Add production rules for '-' and '/'

int top = 0, l;
char prec[9][9] = {
    /*input*/
    /*stack + - * / ^ i ( ) $ */
    /* + */ '>', '>', '<', '<', '<', '<', '<', '>', '>',
    /* - */ '>', '>', '<', '<', '<', '<', '<', '>', '>',
    /* * */ '>', '>', '>', '>', '<', '<', '<', '>', '>',
    /* / */ '>', '>', '>', '>', '<', '<', '<', '>', '>',
    /* ^ */ '>', '>', '>', '>', '<', '<', '<', '>', '>',
    /* i */ '>', '>', '>', '>', '>', 'e', 'e', '>', '>',
    /* ( */ '<', '<', '<', '<', '<', '<', '<', '>', 'e',
    /* ) */ '>', '>', '>', '>', '>', 'e', 'e', '>', '>',
    /* $ */ '<', '<', '<', '<', '<', '<', '<', '<', '>',
};

int main() {
	printf("Choose a parsing method:\n");
    printf("1. LR(0) Parser\n");
    printf("2. Operator Precedence Parser\n");
    int choice;
    scanf("%d", &choice);
	switch (choice) {
        case 1:
            lr0_parser();
            break;
        case 2:
            operator_precedence_parser();
            break;
        default:
            printf("Invalid choice\n");
            break;
    }

    return 0;
}

	


int getindex(char c)
{
    switch (c)
    {
    case '+':
        return 0;
    case '-':
        return 1;
    case '*':
        return 2;
    case '/':
        return 3;
    case '^':
        return 4;
    case 'i':
        return 5;
    case '(':
        return 6;
    case ')':
        return 7;
    case '$':
        return 8;
    }
}

int shift()
{
    inputstack[++top] = *(input + i++);
    inputstack[top + 1] = '\0';
}

int reduce()
{
    int i, len, found, t;
    for (i = 0; i < 8; i++) // selecting handles (including new rules)
    {
        len = strlen(handles[i]);
        if (inputstack[top] == handles[i][0] && top + 1 >= len)
        {
            found = 1;
            for (t = 0; t < len; t++)
            {
                if (inputstack[top - t] != handles[i][t])
                {
                    found = 0;
                    break;
                }
            }
            if (found == 1)
            {
                inputstack[top - t + 1] = 'E';
                top = top - t + 1;
                strcpy(lasthandle, handles[i]);
                inputstack[top + 1] = '\0';
                return 1; // successful reduction
            }
        }
    }
    return 0;
}

void dispstack()
{
    int j;
    for (j = 0; j <= top; j++)
        printf("%c", inputstack[j]);
}

void dispinput()
{
    int j;
    for (j = i; j < l; j++)
        printf("%c", *(input + j));
}

void operator_precedence_parser()
{
    input = (char *)malloc(50 * sizeof(char));
    printf("\nEnter the string\n");
    scanf("%s", input);
    input = strcat(input, "$");
    l = strlen(input);
    strcpy(inputstack, "$");
    printf("\nSTACK\tINPUT\tACTION");
    while (i < l)
    {
        shift();
        printf("\n");
        dispstack();
        printf("\t");
        dispinput();
        printf("\tShift");
        if (prec[getindex(inputstack[top])][getindex(input[i])] == '>')
        {
            while (reduce())
            {
                printf("\n");
                dispstack();
                printf("\t");
                dispinput();
                printf("\tReduced: E->%s", lasthandle);
            }
        }
    }

    if (strcmp(inputstack, "$E$") == 0)
        printf("\nAccepted;");
    else
        printf("\nNot Accepted;");

  
}

void lr0_parser(){
	start();	//Compute closure and goto.

	initialize_first_follow();
	compute_first();
	compute_follow();

	create_parsing_table();

	parse();	//Parse the input string.

}

