@echo off
"C:\\Users\\Moras\\android-sdks\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\Moras\\Desktop\\Proyecto profesional\\TP_Labo_Principal\\Cypher_Vault\\desarrollo\\app\\sdk\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=24" ^
  "-DANDROID_PLATFORM=android-24" ^
  "-DANDROID_ABI=arm64-v8a" ^
  "-DCMAKE_ANDROID_ARCH_ABI=arm64-v8a" ^
  "-DANDROID_NDK=C:\\Users\\Moras\\android-sdks\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Moras\\android-sdks\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Moras\\android-sdks\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Moras\\android-sdks\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\Moras\\Desktop\\Proyecto profesional\\TP_Labo_Principal\\Cypher_Vault\\desarrollo\\app\\sdk\\build\\intermediates\\cxx\\Debug\\2d1b7303\\obj\\arm64-v8a" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\Moras\\Desktop\\Proyecto profesional\\TP_Labo_Principal\\Cypher_Vault\\desarrollo\\app\\sdk\\build\\intermediates\\cxx\\Debug\\2d1b7303\\obj\\arm64-v8a" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\Moras\\Desktop\\Proyecto profesional\\TP_Labo_Principal\\Cypher_Vault\\desarrollo\\app\\sdk\\.cxx\\Debug\\2d1b7303\\arm64-v8a" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
