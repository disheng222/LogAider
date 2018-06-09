set term post eps enh "Arial" 16 color
set output "EPSFILENAME.eps"
#set key inside top left Left reverse
unset key

#width: 20+10x16+20x4x16
#highet: 3x10+3x20+6x10+20x4x6
set xrange [0:4000]
set yrange [0:1475]
#set grid y

set xlabel ""
set ylabel ""

set size 10,9

unset xtics
unset ytics

set style fill solid border -1

set style line 1 lt 2 lc rgb "black" lw 20
set style arrow 1 head nofilled ls 1 size screen 0.06,25.000,120.000

set boxwidth 1

