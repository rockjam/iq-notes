docker pull mvertes/alpine-mongo
docker pull rockjam/iq-notes

mkdir -p ~/iq-notes-mongo

docker rm -f iq-notes-mongo
docker rm -f iq-notes

docker run \
  --name iq-notes-mongo \
  -p 27017:27017 \
  -v ~/iq-notes-mongo:/data/db \
  -d \
  mvertes/alpine-mongo

docker run \
  --name iq-notes \
  -p 3000:3000 \
  --link iq-notes-mongo:mongo \
  -e IQ_NOTES_MONGO_HOST=iq-notes-mongo \
  -d \
  rockjam/iq-notes
