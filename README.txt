README
@author Andrew Dybka
@student# 101041087
@date 02/09/2018
Assignment 2

To Run:
Kitchen.java as java application

Description:
Agent class: extention of thread which has run() that calls put() in shared instance of Kitchen

Chef class: extention of thread with a name and ingredient that call make() in shared instance of Kitchen
to make the sandwich

Kitehcn Class: Make program that creates instance of itself, agent and 3 instances of chef. Starts all 
threads (3 chefs and agent). Put is called by agent thread which only can put 2 random ingredients on table
if table is emtpy. Make is called by chefs to see if they can complete the sandwich.

Steps of execution:
1. Agents selects 2 random ingredients of 3 and places them on table then notifyAll()
2. Chef thread will access class and which ever has the 3rd ingredient will make the sandwich and eat it and notifyAll()
3. Repeat 20 times until 20 sandwichs are made
