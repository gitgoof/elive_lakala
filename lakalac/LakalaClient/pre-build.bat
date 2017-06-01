set build_datetime=%date:~2,2%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%

del /q LakalaClient/src/main/assets/buildtime.tt
@echo %build_datetime% > LakalaClient/src/main/assets/buildtime.tt