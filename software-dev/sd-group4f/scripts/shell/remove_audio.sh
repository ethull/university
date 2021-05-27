for file in *.mkv
do
    ffmpeg -nostdin -i $file -c copy -an "${file%.*}out.mkv"
done
