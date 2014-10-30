#!/bin/bash

datadir="data/"
solndir="soln/"
#Problem 1
echo "Problem 1"
prob="sample_1"
for n in 0 1 2 3 4
do
  infile="${datadir}${prob}_${n}.in"
  outfile="${solndir}${prob}_${n}.out"
  echo "${infile}"
  java -jar SackFiller.jar ${infile} ${outfile}
done
echo

#Problem 2
echo "Problem 2"
prob="sample_2"
for n in 0 1 2 3
do
  infile="${datadir}${prob}_${n}.in"
  outfile="${solndir}${prob}_${n}.out"
  echo "${infile}"
  java -jar SackFiller.jar ${infile} ${outfile}
done
echo

#Problem 3
echo "Problem 3"
prob="sample_3"
for n in 0 1 3 4
do
  infile="${datadir}${prob}_${n}.in"
  outfile="${solndir}${prob}_${n}.out"
  echo "${infile}"
  java -jar SackFiller.jar ${infile} ${outfile}
done
echo


