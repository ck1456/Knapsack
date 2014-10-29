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


