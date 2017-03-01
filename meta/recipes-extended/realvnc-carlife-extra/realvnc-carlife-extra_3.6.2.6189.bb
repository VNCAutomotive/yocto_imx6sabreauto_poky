# Copyright (C) 2017 RealVNC Ltd.  All Rights Reserved.

LICENSE = "CLOSED"

# A python snippet to set variable REALVNC_ARCHIVE_NAME.
# If the file "RealVNC-CarLife-${PV}-Linux-Full.tgz" exists under the directory
# realvnc-carlife-extra, then we use it. Otherwise, we assume that the file
# "RealVNC-CarLife-${PV}-Linux-AOA.tgz" exists.
REALVNC_ARCHIVE_NAME="${@'RealVNC-CarLifeSDK-'+bb.data.getVar('PV',d,1)+'-Linux-Full' if os.path.exists(bb.data.getVar('FILE_DIRNAME',d,1)+'/realvnc-carlife-extra/RealVNC-CarLifeSDK-'+bb.data.getVar('PV',d,1)+'-Linux-Full.tgz') else 'RealVNC-CarLifeSDK-'+bb.data.getVar('PV',d,1)+'-Linux-AOA'}"

SRC_URI = "file://${REALVNC_ARCHIVE_NAME}.tgz"

S_ARCHIVE = "${WORKDIR}/${REALVNC_ARCHIVE_NAME}"

FILES_${PN} = "/"

do_install() {
    install -d ${D}/etc/udev/rules.d
    install -m 0755 ${S_ARCHIVE}/discoverysdk/UdevScripts/etc/udev/linux_dhcp_setup.sh ${D}/etc/udev/
    if [ -f "${S_ARCHIVE}/discoverysdk/UdevScripts/etc/udev/rules.d/99-aoadiscoverer.rules" ]; then
        install -m 0644 ${S_ARCHIVE}/discoverysdk/UdevScripts/etc/udev/rules.d/99-aoadiscoverer.rules ${D}/etc/udev/rules.d/
    fi
    if [ -f "${S_ARCHIVE}/discoverysdk/UdevScripts/etc/udev/rules.d/99-iap2discoverer.rules" ]; then
        install -m 0644  ${S_ARCHIVE}/discoverysdk/UdevScripts/etc/udev/rules.d/99-iap2discoverer.rules ${D}/etc/udev/rules.d/
    fi

	mkdir -p ${D}/etc/modprobe.d
	cat >> ${D}/etc/modprobe.d/ath6kl_core.conf << EOF
# Load ath6kl_core with WiFi P2P enabled.
options ath6kl_core ar6k_clock=26000000 ath6kl_p2p=1 wow_mode=2
EOF
	chmod 0644 ${D}/etc/modprobe.d/ath6kl_core.conf
	mkdir -p ${D}/etc/modules-load.d 
    cat >> ${D}/etc/modules-load.d/libcomposite.conf << EOF
# Load libcomposite to support USB configfs
libcomposite
EOF
    chmod 0644 ${D}/etc/modules-load.d/libcomposite.conf
}

