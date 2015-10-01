# Copyright (C) 2014-2015 RealVNC Ltd.  All Rights Reserved.

LICENSE = "CLOSED"

SRC_URI = "file://mini_x_session"

FILES_${PN} = "/"

S = "${WORKDIR}"

do_install() {
       install -d ${D}/etc/mini_x
       install -m 0755 ${S}/mini_x_session ${D}/etc/mini_x/session
}

