fileInfo=""
for file in *.mkv
do
    if [ "$fileInfo" != "" ]; then
        fileInfo="$fileInfo\nfile '$PWD/$file'"
    else
        fileInfo="file '$PWD/$file'"
    fi
        
done

echo -e $fileInfo > out
#cat out
ffmpeg -f concat -safe 0 -i out -c copy vids_combined.mkv
rm out
