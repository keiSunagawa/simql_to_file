setup: simqlToCsv/simql
	git submodule update -i
	cd simqlToCsv && git submodule update -i
release:
	mkdir release
	cargo build --release && cp target/release/simql_to_file release/
	cd simqlToCsv/simqltocsv && sbt assembly && cp target/scala-2.12/simqlToCsv-assembly-0.1.0-SNAPSHOT.jar ../../release/

clean:
	rm -rf release
