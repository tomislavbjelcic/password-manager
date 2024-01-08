# Password Manager

Simple password manager written in Java. Laboratory exercise assignment for course [Computer Security](https://www.fer.unizg.hr/predmet/srs). Implementation details are described in [opis.txt](opis.txt) (in croatian).

## Prerequisites

Make sure you have installed all of the following:
* Java - JDK >=11. Make sure to set the `JAVA_HOME` environment variable pointing to your JDK installation directory and make sure to have `bin` directory added to `PATH` environment variable.
* Maven - set the `M2_HOME` environment variable pointing to your Maven installation. Add the `bin` directory to `PATH` environment variable.

## Build

After cloning the repository, simply execute the following command:
```shell
mvn compile
```


## Usage

Password manager is designed as a command line program with command line arguments. It is advised to use a simple script `pwmgr` which replaces the `java -cp target/classes/ srs.lab1.Main` every time the tool is used. If you wish to use `pwmgr` script, make sure to allow execution permission:
```shell
chmod +x pwmgr
```

Password manager has 3 supported commands:
* `init` - initialize the encrypted vault with master password.
* `put` - add another pair of address and password.
* `get` - get the password of given address.

Master password that was used in `init` has to be provided for `put` and `get` commands. All passwords are protected with that master password. If the provided master password is not correct or the encrypted vault has been tampered with, `put` and `get` commands will not proceed further.

Usage demonstration using master password `mpwrd`:
```shell
$ ./pwmgr init mpwrd
Command init: password manager initialized.

$ ./pwmgr put mpwrd www.github.com strawberries12345
Command put: Stored password for www.github.com.

$ ./pwmgr get mpwrd www.github.com
Command get: Password for www.github.com is: strawberries12345

$ ./pwmgr get INCORRECT www.github.com
Command get: unable to retrieve password for www.github.com.
Either master password was wrong or encrypted files have been modified.

$ ./pwmgr get mpwrd www.nonexistent.com
Command get: No record for www.nonexistent.com.
```

