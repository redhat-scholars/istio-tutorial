FROM registry.access.redhat.com/dotnet/dotnet-20-runtime-rhel7

ADD bin/Release/netcoreapp2.0/rhel.7-x64/publish/. /app/

WORKDIR /app/

EXPOSE 5000 

CMD ["scl", "enable", "rh-dotnet20", "--", "dotnet",  "preference.dll"]
