# Readme

## 项目简介

该项目为华中科技大学管理学院省级大创项目“智能排课优化算法的探索研究”项目仓库。该仓库包含项目内算法相关代码，包括整数规划模型代码以及启发式算法代码。

## 内容摘要

在高校教学信息中，课程安排是一项重要而又复杂的基础性工作。随着课程数量不断增加，课程类别和授课形式不断改变，排课问题变得愈发复杂。因此，设计合适的算法利用计算机解决排课问题已成为当务之急。本文针对华中科技大学管理学院排课现状，经过实地调研访谈、分析数据，对排课管理中存在的关键问题进行了梳理和建模。在此基础上，针对小规模算例建立了可以精确求解最优解的混合整数规划模型。模型通过在目标函数中引入惩罚、奖励值，在满足硬约束的前提下尽可能减少软约束的违背次数。针对较难求得最优解的大规模算例，设计了一套两阶段元启发式算法，在较短时间内高效求解问题。设计的两阶段算法结合图着色算法将课程分类优化硬约束冲突，并结合禁忌搜索算法优化软约束，得到近似最优解。测试结果表明，我们的模型和算法能够有效求解实际问题。

**关键词**： 排课，混合整数规划模型，禁忌搜索，图着色

## 项目成员

负责人：周航

项目成员：朱正雄，胡心瑶，李璠，向柯玮

指导老师：秦虎

其中，模型代码由胡心瑶编写，算法代码由朱正雄编写。



**项目仅作学习使用，未经允许，不得抄袭！**

**更新日期：2022年4月4日**





## Introduction

This repository is for a Provincial College Students’ innovation and entrepreneurship training program "a research of optimization algorithms for the university course scheduling problem", in Huazhong University of Science and Technology, school of management. We now open the code in our program, including the mix-integer linear programming and a two-stage hybrid meta-heuristic algorithm.

## Abstract

Course scheduling plays an important role in the universities' information systems. As the number of courses increases rapidly, the course scheduling problem becomes more and more difficult to solve. Therefore, designing an effective and efficient algorithm to solve the problem by computers is a necessity. This work aims to solve the University Course Scheduling Problem in Huazhong University of Science and Technology, School of Management. We first interview the school's administrators and model the problem, then analyze the actual problem data in a semester to build the instances. Mix-Integer Linear Programming model based on penalty and reward value is proposed to solve the small-sized instance to optimality. For large-sized instances, we propose a two-stage meta-heuristic algorithm based on graph coloring and tabu search to obtain a high-quality solution in an acceptable time. Numerical experiments show the effectiveness of our model and algorithm.

**Key words:** course scheduling, Mix-Integer Linear Programming, Tabu Search, graph coloring

## Authors

Principal: Hang Zhou

Members: Zhengxiong Zhu, Xinyao Hu, Fan li, Kewei Xiang

Adviser: Hu Qin

The model is programmed by Xinyao Hu, and the algorithm is programmed by Zhengxiong Zhu.

**The project is for study only. Any plagiarism is not allowed without permission.**

**Update: 2022, April, 4th**
