filename = "300d0_1000d0_10d0_100d0_"
cfilename = filename."c"

time1 = "10.0"
time2 = "t10.0_"

col = 11
colf = 12

#############################

set term postscript eps enhanced color "Helvetica" 20
set output time2.cfilename.".eps"

set xlabel "Volume of traffic X(t)"
set ylabel "cumulative distribution"

#枠線の設定．1(下),2(左),4(上),8,(右)
set border 3

#縦横比を固定する
set size ratio 0.5

#凡例
set key left top

#範囲指定
set xrange [0:1000]
set yrange [0.0:1.0]

#目盛指定（nomirrorで目盛を消す）
set xtics 200 nomirror
set ytics 0.2 nomirror

set label 1 'time = '.time1 at 670, 0.2 center
#set label 1 font 'Roman, 24'

plot './fp_w/fp_'.cfilename.'.dat' using 1:colf title "Fokker-Planck" with line lt 1 lw 5 ,\
	 'simulation_h3/'.cfilename.'.dat' using 1:col title "Monte-Carlo" with line lt 3 lw 5