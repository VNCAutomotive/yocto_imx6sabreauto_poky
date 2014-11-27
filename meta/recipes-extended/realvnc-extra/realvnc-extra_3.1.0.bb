LICENSE = "CLOSED"

REALVNC_BUILD_NUMBER="7943"
REALVNC_ARCHIVE_NAME="RealVNC-ViewerSDK-${PV}.${REALVNC_BUILD_NUMBER}-Linux-automotive"

SRC_URI = "file://${REALVNC_ARCHIVE_NAME}.tar.gz"

S_FIRMWARE = "${WORKDIR}"
S_ARCHIVE = "${WORKDIR}/${REALVNC_ARCHIVE_NAME}"
S = "${S_ARCHIVE}/discoverysdk/UdevScripts"

FILES_${PN} = "/"

do_install() {
	install -d ${D}/usr/bin
	install -m 0755 ${S}/usr/bin/wpa_dhcp_server ${D}/usr/bin/
	install -m 0755 ${S}/usr/bin/wpa_dhcp_client ${D}/usr/bin/
	install -d ${D}/etc/udev/rules.d
	install -m 0755 ${S}/etc/udev/linux_dhcp_setup.sh ${D}/etc/udev/
	install -m 0755 ${S}/etc/udev/wfd_wpa_supplicant_setup.sh ${D}/etc/udev/
	install -m 0644 ${S}/etc/udev/rules.d/99-mirrorlink.rules ${D}/etc/udev/rules.d/
	install -m 0644 ${S}/etc/udev/rules.d/99-usbdiscoverer.rules ${D}/etc/udev/rules.d/
	install -m 0644 ${S}/etc/udev/rules.d/99-wfd.rules ${D}/etc/udev/rules.d/
}

