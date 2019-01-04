# Copyright (c) 2012-2016 Vige, Italy
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the GNU GENERAL PUBLIC LICENSE
# which accompanies this distribution, and is available at
# http://fsf.org/
# Contributors:
# Vige, Italy. - Docker images

FROM openjdk
EXPOSE 4403 8000 8080 9876 22
RUN apt-get update && \
	apt-get -y install sudo openssh-server && \
    mkdir /var/run/sshd && \
    sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd && \
    echo "%sudo ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers && \
    useradd -u 1000 -G users,sudo -d /home/user --shell /bin/bash -m user && \
    echo "user:secret" | chpasswd && \
    apt-get update && \
    apt-get clean && \
    apt-get -y autoremove && \
    rm -rf /var/lib/apt/lists/*

USER user

LABEL che:server:8080:ref=wildfly che:server:8080:protocol=http che:server:8000:ref=wildfly-debug che:server:8000:protocol=http che:server:9876:ref=codeserver che:server:9876:protocol=http

ENV MAVEN_VERSION=3.6.0 \
    JBOSS_HOME=/home/user/wildfly15 \
    WILDFLY_VERSION=15.0.0.Final

ENV M2_HOME=/home/user/apache-maven-$MAVEN_VERSION

ENV PATH=$M2_HOME/bin:$PATH

RUN mkdir $JBOSS_HOME /home/user/apache-maven-$MAVEN_VERSION && \
  wget -qO- "http://apache.ip-connect.vn.ua/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" | tar -zx --strip-components=1 -C /home/user/apache-maven-$MAVEN_VERSION/
ENV TERM xterm

RUN wget -qO- "http://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz" | tar -zx --strip-components=1 -C $JBOSS_HOME && $JBOSS_HOME/bin/standalone.sh &

ENV LANG it_IT.UTF-8
WORKDIR /projects

CMD sudo /usr/sbin/sshd -D && \
    tail -f /dev/null