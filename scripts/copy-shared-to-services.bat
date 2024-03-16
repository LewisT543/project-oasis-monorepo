@echo off

del /S /Q "..\propertyService\app\oasisSharedLibrary\"
xcopy /Y "..\oasisSharedLibrary\src\main\scala\*" "..\propertyService\app\" /s /e